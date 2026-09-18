package com.myapp.mysimon.ui.screens.game

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapp.mysimon.R
import com.myapp.mysimon.ui.theme.*

/**
 * The main game interface.
 * Displays the 3x2 grid of colored buttons, the current sequence progress,
 * and the game control buttons (Start, Pause, End Game).
 * Adapts its layout based on screen orientation (Portrait or Landscape).
 */
@Composable
fun GameScreen(
    gameState: GameState, // Actual state of the game
    text: String, // String with the sequence of the actual game
    activeButtonIndex: Int, // Index of the button that should be illuminated
    onColoredButtonClick: (Int) -> Unit, // Function used to handle the click on a coloured button
    onStartButtonClick: () -> Unit, // Function used to start a new game
    onPauseButtonClick: () -> Unit, // Function used to pause (or resume if already paused) the current game
    onEndgameButtonClick: () -> Unit // Function used to end the current game and return to the first screen
) {
    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        // Vertical layout for portrait mode
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonGrid(
                modifier = Modifier.weight(0.7f),
                gameState = gameState,
                activeButtonIndex = activeButtonIndex,
                onButtonClick = onColoredButtonClick
            )

            SequenceText(
                modifier = Modifier.weight(0.2f),
                sequence = text,
                gameState = gameState
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
            ) {
                StartButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onStartButtonClick,
                    gameState = gameState
                )
                PauseButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onPauseButtonClick,
                    gameState = gameState
                )
                EndgameButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onEndgameButtonClick,
                    gameState = gameState
                )
            }
        }
    } else {
        // Horizontal layout for landscape mode
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonGrid(
                modifier = Modifier.weight(1f),
                gameState = gameState,
                activeButtonIndex = activeButtonIndex,
                onButtonClick = onColoredButtonClick
            )

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SequenceText(
                    modifier = Modifier.weight(2f),
                    sequence = text,
                    gameState = gameState
                )

                StartButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onStartButtonClick,
                    gameState = gameState
                )
                PauseButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onPauseButtonClick,
                    gameState = gameState
                )
                EndgameButton(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    onButtonClick = onEndgameButtonClick,
                    gameState = gameState
                )
            }
        }
    }
}

/**
 * A 3x2 grid of buttons representing the colors the user must follow.
 */
@Composable
fun ButtonGrid(
    modifier: Modifier = Modifier,
    gameState: GameState,
    activeButtonIndex: Int,
    onButtonClick: (Int) -> Unit
) {
    val colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Yellow, Color.Blue, Color.Cyan)

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        var index = 0
        repeat(3) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(2) {
                    val i = index
                    val isButtonActive = (i == activeButtonIndex)
                    // Dim the button color if it's not currently active (illuminated)
                    val buttonColors = if (isButtonActive) colors[i] else colors[i].copy(alpha = 0.3f)

                    Button(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = { onButtonClick(i) },
                        // Buttons are only clickable during the user's turn
                        enabled = gameState == GameState.USER_TURN,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(2.dp, Color.Black),
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

/**
 * Display for the current game sequence or system messages (e.g., Error, New Sequence).
 */
@Composable
fun SequenceText(
    modifier: Modifier = Modifier,
    sequence: String,
    gameState: GameState
) {
    val scrollState = rememberScrollState()
    val newSequence = stringResource(R.string.new_sequence)
    val error = stringResource(R.string.error)

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Yellow, Color.Blue, Color.Cyan),
        startX = 0.0f,
        endX = 500.0f,
        tileMode = TileMode.Repeated
    )

    Text(
        text = when (gameState) {
            GameState.STARTING -> newSequence
            GameState.GAME_OVER -> error
            else -> sequence
        },
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .background(color = LightBlueGrey50, shape = RoundedCornerShape(4.dp))
            .border(width = 1.dp, brush = gradientBrush, shape = RoundedCornerShape(4.dp)),
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

/**
 * Button to initialize a new game session.
 */
@Composable
fun StartButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    val start = stringResource(R.string.new_game)
    Button(
        modifier = modifier.padding(4.dp),
        onClick = onButtonClick,
        enabled = gameState == GameState.STARTING,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = start,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Button to toggle the pause state during the sequence playback.
 */
@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    val pause = stringResource(R.string.pause)
    val resume = stringResource(R.string.resume)
    Button(
        modifier = modifier.padding(4.dp),
        onClick = onButtonClick,
        enabled = (gameState == GameState.CPU_TURN) || (gameState == GameState.PAUSE),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = if (gameState == GameState.PAUSE) resume else pause,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Button to force-end the current game session.
 */
@Composable
fun EndgameButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    gameState: GameState
) {
    val endgame = stringResource(R.string.endgame)
    Button(
        modifier = modifier.padding(4.dp),
        onClick = onButtonClick,
        enabled = (gameState == GameState.USER_TURN) ||
                  (gameState == GameState.CPU_TURN) ||
                  (gameState == GameState.PAUSE),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = endgame,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(
        gameState = GameState.USER_TURN,
        text = "R, G, B, Y, M, R, B, B",
        activeButtonIndex = 2,
        onColoredButtonClick = {},
        onStartButtonClick = {},
        onPauseButtonClick = {},
        onEndgameButtonClick = {}
    )
}
