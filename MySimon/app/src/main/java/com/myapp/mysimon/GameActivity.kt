package com.myapp.mysimon

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.myapp.mysimon.ui.theme.*

class GameActivity : ComponentActivity() {

    private lateinit var gameViewModel: GameViewModel
    private val mTag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        // Get a new or existing ViewModel from the ViewModelProvider
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        Log.d(mTag, "Ho accesso al viewmodel")

        // Set and display the UI content
        setContent {
            // Collect the actual state of the game
            val gameState by gameViewModel.gameState.collectAsState()
            val text by gameViewModel.sequenceString.collectAsState()
            val activeButtonIndex by gameViewModel.activeButtonIndex.collectAsState()

            MySimonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
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
                            this.finish()
                        }
                    )
                }
            }
        }
    }
}

// Function of the game screen of the app
// Contains colored buttons, current sequence and the menu buttons
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameState: GameState, // Actual state of the game
    text: String, // String with the sequence of the actual game
    activeButtonIndex: Int, // Index of the button that should be illuminated
    onColoredButtonClick: (Int) -> Unit, // Function used to handle the click on a coloured button
    onStartButtonClick: () -> Unit, // Function used to start a new game
    onPauseButtonClick: () -> Unit, // Function used to pause (or resume if already paused) the current game
    onEndgameButtonClick: () -> Unit // Function used to end the current game and return to the first activity
) {
    // Orientation of the device
    val orientation = LocalConfiguration.current.orientation

    // Default string that appears in the text box before a game is started
    val newSequence = stringResource(R.string.new_sequence)

    // Layout of the game activity
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        // Layout for the portrait mode
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // On top the button grid cover 4/7 of the total space
            ButtonGrid(
                modifier = Modifier
                    .weight(4f),
                gameState = gameState,
                activeButtonIndex = activeButtonIndex,
                onButtonClick = onColoredButtonClick
            )

            // Under the grid there is the box with the current sequence, it cover 2/7 of the total space
            SequenceText(
                modifier = Modifier
                    .weight(2f),
                sequence = if (gameState == GameState.STARTING) newSequence else text
            )

            // On the bottom there is a row with the three game buttons, covering the last 1/7 of space
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                StartButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onStartButtonClick,
                    gameState = gameState
                )
                PauseButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onPauseButtonClick,
                    gameState = gameState
                )
                EndgameButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onEndgameButtonClick,
                    gameState = gameState
                )
            }
        }
    } else {
        // Layout for the landscape mode
        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // On the left there is the grid covering half of the screen
            ButtonGrid(
                modifier = Modifier
                    .weight(1f),
                gameState = gameState,
                activeButtonIndex = activeButtonIndex,
                onButtonClick = onColoredButtonClick
            )

            // On the right there are the rest of the items, everyone in the same column, covering the other half of the screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // On top there is the sequence of the current game
                SequenceText(
                    modifier = Modifier
                        .weight(2f),
                    sequence = if (gameState == GameState.STARTING) newSequence else text
                )

                // Under the sequence there are the three button
                StartButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onStartButtonClick,
                    gameState = gameState
                )
                PauseButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onPauseButtonClick,
                    gameState = gameState
                )
                EndgameButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onEndgameButtonClick,
                    gameState = gameState
                )
            }
        }
    }
}

// Composable function that define the 3x2 matrix of colored buttons
// In the parameters is passed the state, the index of the button clicked and the function called when a button is clicked
@Composable
fun ButtonGrid(
    modifier: Modifier = Modifier,
    gameState: GameState,
    activeButtonIndex: Int,
    onButtonClick: (Int) -> Unit
) {
    // All the buttons colors
    val colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Yellow, Color.Blue, Color.Cyan)

    // Column that contain the 3x2 matrix with the 6 buttons
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Loop used to create the 6 buttons inside the column
        // All the rows have the same weight, as do the buttons which are therefore the same size
        var index = 0
        repeat(3) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(2) {
                    // Save the current index
                    val i = index

                    // Illuminates the button if it is the one indicated
                    val isButtonActive = (i == activeButtonIndex)

                    // Check if it's the user turn
                    val isUserTurn = (gameState == GameState.USER_TURN)

                    // The button is darker (at 40%) if the button is inactive
                    val buttonColors = if (isButtonActive) colors[i] else colors[i].copy(alpha = 0.4f)

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            onButtonClick(i)
                        },
                        enabled = isUserTurn,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColors,
                            disabledContainerColor = buttonColors
                        )
                    ) {}
                    index++
                }
            }
        }
    }
}

// Composable function that display the sequence of the current game
// If the sequence is too long for its box, it will be scrollable
@Composable
fun SequenceText(
    modifier: Modifier = Modifier,
    sequence: String
) {
    // Value used to make the sequence scrollable and not expandable
    val scrollState = rememberScrollState()

    // Gradient border color for the text box
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Yellow, Color.Blue, Color.Cyan),
        startX = 0.0f,
        endX = 500.0f,
        tileMode = TileMode.Repeated
    )

    // Text view with the string of the current game
    Text(
        text = sequence,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .background(color = LightBlueGrey50, shape = RoundedCornerShape(4.dp))
            .border(width = 1.dp, brush = gradientBrush, shape = RoundedCornerShape(4.dp)),
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )
}

// Composable function that define the button Start, used to start a new game
// In the parameters is passed the function called when the button is clicked
@Composable
fun StartButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    // String of the button
    val start = stringResource(R.string.new_game)

    // Button to start a new game
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
        enabled = gameState == GameState.STARTING,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = start,
            fontSize = 16.sp
        )
    }
}

// Composable function that define the button Pause, used to pause the sequence the app is generating
// In the parameters is passed the function called when the button is clicked
@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    // Strings of the button
    val pause = stringResource(R.string.pause)
    val resume = stringResource(R.string.resume)

    // Button to pause the current game
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
        enabled = (gameState == GameState.CPU_TURN) || (gameState == GameState.PAUSE),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            // The text change depending on the state of the game
            text = if (gameState == GameState.PAUSE) resume else pause,
            fontSize = 16.sp
        )
    }
}

// Composable function that define the button End Game, used to end the current game
// In the parameters is passed the function called when the button is clicked
@Composable
fun EndgameButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    // String of the button
    val endgame = stringResource(R.string.endgame)

    // Button to end the current game
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
        enabled = gameState != GameState.STARTING,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = endgame,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(
        gameState = GameState.STARTING,
        text = "R, G, B, Y, M, R, B, B",
        activeButtonIndex = 2,
        onColoredButtonClick = {},
        onStartButtonClick = {},
        onPauseButtonClick = {},
        onEndgameButtonClick = {}
    )
}
