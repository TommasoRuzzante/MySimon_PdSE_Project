package com.myapp.mysimon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapp.mysimon.data.*
import com.myapp.mysimon.ui.theme.*

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        // Initialize the database and the repository to access the database
        val db = AppDatabase.getDatabase(this)
        val repository = GameRepository(db.gameDao())

        // Get the id of the game from the intent
        val id = intent.getIntExtra("gameID", 0)

        // Set and display the UI content
        setContent {
            MySimonTheme {
                // Define the default value of the game we want to display
                var game by remember { mutableStateOf<Game?>(null) }

                // Start a coroutine to search the game in the database
                LaunchedEffect(id) {
                    game = repository.selectGame(id)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Wait the finish of the coroutine to pass the game to the screen function
                    val currentGame = game
                    if (currentGame != null) {
                        // When the game is ready, display the detail screen
                        DetailScreen(
                            game = currentGame
                        )
                    } else {
                        // While waiting, display a loading screen
                        Text("Loading...", modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

// Function of the detail screen of the app
// Contain the full sequence of the game and the maximum number of consecutive correct clicks
@Composable
fun DetailScreen(
    game: Game // The game we want to display
) {
    // String used on this activity
    val details = stringResource(R.string.game_details)

    // The layout of the endgame activity is contained in a column in portrait and landscape too
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title of the page
        Text(
            text = details,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // Text with the score of this game
        Text(
            text = game.counter.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Display the sequence of this game
        DetailedSequence(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            sequence = game.sequence,
            error = game.error
        )
    }
}

// Composable function that define the text view of the screen
@Composable
fun DetailedSequence(
    modifier: Modifier = Modifier,
    sequence: String,
    error: Int
) {
    // Sequence of this game divided in green part and red part
    val errorSplitIndex = (3 * (error - 1)).coerceIn(0, sequence.length)
    val resultString = buildAnnotatedString {
        append(sequence)
        // Add the green color to the correct part
        addStyle(
            style = SpanStyle(Color.Green),
            start = 0,
            end = errorSplitIndex
        )
        // Add the red color to the wrong part
        addStyle(
            style = SpanStyle(Color.Red),
            start = errorSplitIndex,
            end = sequence.length
        )
    }

    // Value used to make the sequence scrollable and not expandable
    val scrollState = rememberScrollState()

    // Text with the sequence of this game
    Text(
        text = resultString,
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(Modifier, Game(counter = 8, sequence = "R, G, B, Y, M, R, B, B", error = 6))
}