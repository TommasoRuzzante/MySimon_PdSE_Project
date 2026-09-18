package com.myapp.mysimon.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.myapp.mysimon.ui.screens.account.AccountScreen
import com.myapp.mysimon.ui.screens.account.AccountViewModel
import com.myapp.mysimon.ui.screens.detail.DetailScreen
import com.myapp.mysimon.ui.screens.detail.DetailViewModel
import com.myapp.mysimon.ui.screens.game.GameScreen
import com.myapp.mysimon.ui.screens.game.GameState
import com.myapp.mysimon.ui.screens.game.GameViewModel
import com.myapp.mysimon.ui.screens.home.HomeScreen
import com.myapp.mysimon.ui.screens.home.HomeViewModel

/**
 * The central navigation hub for the application.
 * Defines the mapping between routes (Destinations) and their corresponding Composable screens.
 * Manages ViewModel instantiation for each screen and passes down necessary callbacks.
 */
@Composable
fun MySimonNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    playButtonSound: (Int) -> Unit, // Callback to trigger button sound effects
    playEndGameSound: () -> Unit,   // Callback to trigger the game-over sound
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        // --- Home Screen ---
        composable<HomeRoute> {
            val homeViewModel: HomeViewModel = viewModel()
            val gamesList by homeViewModel.games.collectAsState()
            val user by homeViewModel.user.collectAsState()

            HomeScreen(
                user = user,
                buttonDetailScreen = { gameId ->
                    navController.navigate(DetailRoute(gameId))
                },
                buttonAccountScreen = {
                    navController.navigate(AccountRoute)
                },
                games = gamesList
            )
        }

        // --- Game Play Screen ---
        composable<GameRoute> {
            val gameViewModel: GameViewModel = viewModel()

            val gameState by gameViewModel.gameState.collectAsState()
            val text by gameViewModel.sequenceString.collectAsState()
            val activeButtonIndex by gameViewModel.activeButtonIndex.collectAsState()

            // Trigger audio feedback when a button is highlighted or clicked
            LaunchedEffect(activeButtonIndex) {
                if (activeButtonIndex != -1) {
                    playButtonSound(activeButtonIndex)
                }
            }

            // Trigger end-game sound when the game state transitions to GAME_OVER
            LaunchedEffect(gameState) {
                if (gameState == GameState.GAME_OVER) {
                    playEndGameSound()
                }
            }

            // Intercept system back gestures to properly conclude and save the game state
            BackHandler(
                enabled = (gameState != GameState.STARTING) && (gameState != GameState.GAME_OVER)
            ) {
                navController.popBackStack()
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

        // --- Game History Detail Screen ---
        composable<DetailRoute> {
            val detailViewModel: DetailViewModel = viewModel()
            val game by detailViewModel.game.collectAsState()

            BackHandler {
                navController.popBackStack()
            }

            val currentGame = game
            if (currentGame != null) {
                DetailScreen(game = currentGame)
            } else {
                // Temporary loading state while database query completes
                Text("Loading...")
            }
        }

        // --- User Account & Stats Screen ---
        composable<AccountRoute> {
            val accountViewModel: AccountViewModel = viewModel()
            val bestScore by accountViewModel.bestScore.collectAsState()
            val gamesPlayed by accountViewModel.gamesPlayed.collectAsState()
            val user by accountViewModel.user.collectAsState()

            AccountScreen(
                user = user,
                bestScore = bestScore,
                gamesPlayed = gamesPlayed,
                onSaveName = { newName ->
                    accountViewModel.updateName(newName)
                }
            )
        }
    }
}
