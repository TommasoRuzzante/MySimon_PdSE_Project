package com.myapp.mysimon

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapp.mysimon.audio.GameAudioManager
import com.myapp.mysimon.data.*
import com.myapp.mysimon.ui.theme.*

class MainActivity : ComponentActivity() {

    // Instance of the view model, will be initialized later
    private lateinit var gameViewModel: GameViewModel

    // Instance of the audio manager, will be initialized later
    private lateinit var gameAudioManager: GameAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get a new or existing ViewModel from the ViewModelProvider
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        // Initialize the audio manager
        gameAudioManager = GameAudioManager(this)

        // Initialize the database and the repository to access the database
        val db = AppDatabase.getDatabase(this)
        val repository = GameRepository(db.gameDao())

        // Set and display the UI content
        setContent {
            MySimonTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        // Insert the floating action button and by default put it in the bottom right corner
                        FabNewGame(onButtonClick = {
                            navController.navigate("game")
                        })
                    }
                ) { innerPadding ->
                    // Collect the list of games from the database
                    val gamesList by repository.getAllGames().collectAsState(initial = emptyList())

                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("main") {
                            MainScreen(
                                buttonDetailScreen = { game ->
                                    navController.navigate("detail")
                                },
                                buttonAccountScreen = {
                                    navController.navigate("account")
                                },
                                games = gamesList.reversed() // The list is reversed to show the most recent games first
                            )
                        }
                        composable("game") {
                            // Collect the actual state of the game
                            val gameState by gameViewModel.gameState.collectAsState()
                            val text by gameViewModel.sequenceString.collectAsState()
                            val activeButtonIndex by gameViewModel.activeButtonIndex.collectAsState()

                            // Audio feedback for the user when he pressed a colored button
                            LaunchedEffect(activeButtonIndex) {
                                if (activeButtonIndex != -1) {
                                    gameAudioManager.playSound(activeButtonIndex)
                                }
                            }

                            // Audio feedback when the game end
                            LaunchedEffect(gameState) {
                                if (gameState == GameState.GAME_OVER) {
                                    gameAudioManager.playSound(99)
                                }
                            }

                            // Handle the saving of the game when the user press the back button during a game
                            BackHandler(
                                enabled = (gameState != GameState.STARTING) && (gameState != GameState.GAME_OVER)
                            ) {
                                gameViewModel.endGame()
                            }

                            GameScreen(
                                gameState = gameState,
                                text = text,
                                activeButtonIndex = activeButtonIndex,
                                onColoredButtonClick = { btn ->
                                    gameViewModel.userClick(btn)
                                },
                                onStartButtonClick = {
                                    gameViewModel.startNewGame()
                                },
                                onPauseButtonClick = {
                                    if (gameState == GameState.PAUSE) {
                                        gameViewModel.resumeGame()
                                    } else {
                                        gameViewModel.pauseGame()
                                    }
                                },
                                onEndgameButtonClick = {
                                    gameViewModel.endGame()
                                }
                            )
                        }
                        composable("detail/{message}") { backStackEntry ->
                            // Define the default value of the game we want to display
                            var game by remember { mutableStateOf<Game?>(null) }

                            // Start a coroutine to search the game in the database
                            LaunchedEffect(id) {
                                game = repository.selectGame(id)
                            }

                            val currentGame = game
                            if (currentGame != null) {
                                // When the game is ready, display the detail screen
                                DetailScreen(
                                    game = currentGame
                                )
                            } else {
                                // While waiting, display a loading screen
                                Text("Loading...")
                            }
                            /*DetailScreen(
                                message = Uri.decode(backStackEntry.arguments?.getString("message").orEmpty())
                            )*/
                        }
                        composable("account") {
                            AccountScreen()
                        }
                    }
                }
            }
        }
    }

    // The override of onDestroy() is important to avoid memory leaks by releasing the audio manager
    override fun onDestroy() {
        super.onDestroy()
        if (this::gameAudioManager.isInitialized) {
            gameAudioManager.release()
        }
    }
}

