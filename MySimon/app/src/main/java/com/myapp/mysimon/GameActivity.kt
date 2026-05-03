package com.myapp.mysimon

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.myapp.mysimon.data.*
import com.myapp.mysimon.ui.theme.*

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        // Get the database instance and the data access object
        val db = AppDatabase.getDatabase(this)
        val gameDao = db.gameDao()

        // Set and display the UI content
        setContent {
            MySimonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        buttonAction = { sequenceList, counterList ->
                            // The argument passed lists are converted into arrays to be passed to the second activity
                            val sequenceListArray = sequenceList.toTypedArray()
                            val counterListArray = counterList.toIntArray()
                            val intent = Intent(this, MainActivity::class.java).apply {
                                putExtra("sequenceList", sequenceListArray)
                                putExtra("counterList", counterListArray)
                            }
                            // Start of the second activity
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

// Function of the first screen of the app
// Contains colored buttons, current sequence, delete button and end-game button
@Composable
fun GameScreen(modifier: Modifier = Modifier, buttonAction : (List<String>, List<Int>) -> Unit) {
    // Orientation of the device
    val orientation = LocalConfiguration.current.orientation

    // String with the sequence of the actual game
    val newSequence = stringResource(R.string.new_sequence)
    var t by rememberSaveable { mutableStateOf(newSequence) }

    // Boolean value that identifies if a new game is started
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    // Value used to count the actual clicks on buttons
    var count by rememberSaveable { mutableIntStateOf(0) }

    // Lists of the previous games in the current session
    var sequenceList by rememberSaveable {mutableStateOf(listOf<String>())}
    var counterList by rememberSaveable {mutableStateOf(listOf<Int>())}

    // Function used to add the letter of the clicked button to the sequence
    val onColoredButtonCLick: (String) -> Unit = { color->
        // If this button start a new game: reset the sequence, else add the comma before the new letter
        if (!gameStarted) {
            t = ""
            gameStarted = true
        } else {
            t += ", "
        }
        // Add the letter to the sequence and increase the counter
        t += color
        count++
    }

    // Function used to delete the current game and restart a new one
    val onPauseButtonClick: () -> Unit = {
        // Reset the current game
        gameStarted = false
        t = newSequence
        count = 0
    }

    // Function used to end the current game and go to the second activity
    val onEndgameButtonClick: () -> Unit = {
        // Set the sequence to a void string if this game has no button pressed
        if (!gameStarted) {
            t = ""
        }
        // Insert the string and the counter in the lists
        sequenceList += t
        counterList += count
        // Reset the game
        gameStarted = false
        t = newSequence
        count = 0
        // Pass the lists to the second activity
        buttonAction(sequenceList, counterList)
    }

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
                onButtonClick = onColoredButtonCLick
            )

            // Under the grid there is the box with the current sequence, it cover 2/7 of the total space
            SequenceText(
                modifier = Modifier
                    .weight(2f),
                sequence = t
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
                    onButtonClick = {t = "palle"}
                )
                PauseButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onPauseButtonClick
                )
                EndgameButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onEndgameButtonClick
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
                onButtonClick = onColoredButtonCLick
            )

            // On the right there are the rest of the items, everyone in the same column, covering the other half of the screen
            //
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
                    sequence = t
                )

                StartButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = {}
                )

                PauseButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onPauseButtonClick
                )

                EndgameButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onButtonClick = onEndgameButtonClick
                )
            }
        }
    }
}

// Composable function that define the 3x2 matrix of colored buttons
// In the parameters is passed the function called when a button is clicked
@Composable
fun ButtonGrid(modifier: Modifier = Modifier, onButtonClick: (String) -> Unit) {
    // All button colors and their respective initial letters
    val colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Yellow, Color.Blue, Color.Cyan)
    val colorsLetters = listOf("R", "M", "G", "Y", "B", "C")

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
                    val i = index
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onButtonClick(colorsLetters[i]) },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors[index])
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
fun SequenceText(modifier: Modifier = Modifier, sequence: String) {
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

//
@Composable
fun StartButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    // String of the button
    val start = stringResource(R.string.new_game)

    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = start,
            fontSize = 16.sp
        )
    }
}

// Composable function that define the menu button Pause
// In the parameters is passed the function called when the button is clicked
@Composable
fun PauseButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    // String of the button
    val pause = stringResource(R.string.pause)

    // Button to delete the current game
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
    ) {
        Text(
            text = pause,
            fontSize = 16.sp
        )
    }
}

// Composable function that define the menu button End Game
// In the parameters is passed the function called when the button is clicked
@Composable
fun EndgameButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    // String of the button
    val endgame = stringResource(R.string.endgame)

    // Button to end the current game
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = onButtonClick,
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
    GameScreen( buttonAction = { _, _ -> } )
}