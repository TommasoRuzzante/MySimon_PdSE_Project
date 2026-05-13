package com.myapp.mysimon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.lifecycleScope
import com.myapp.mysimon.data.*
import com.myapp.mysimon.ui.theme.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        // Get the database instance and the data access object
        val db = AppDatabase.getDatabase(this)
        val gameDao = db.gameDao()

        // Get the id of the game from the intent
        val id = intent.getIntExtra("gameID", 0)

        // Set and display the UI content
        setContent {
            MySimonTheme {
                // Define the default value of the game we want to display
                var game by rememberSaveable { mutableStateOf<Game?>(null) }

                // Start a coroutine to search the game in the database
                LaunchedEffect(id) {
                    game = gameDao.selectById(id)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Wait the finish of the coroutine to pass the game to the screen function
                    game?.let { thisGame ->
                        DetailScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            game = thisGame
                        )
                    }
                }
            }
        }
    }
}

// Function of the detail screen of the app
// Contain details about a sequence
@Composable
fun DetailScreen(modifier: Modifier = Modifier, game: Game) {
    // String used on this activity
    val details = stringResource(R.string.game_details)

    // The layout of the endgame activity is contained in a column in portrait and landscape too
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
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

        // Number of current clicks in this game
        Text(
            text = game.counter.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Sequence of this game divided in green part and red part
        val resultString = buildAnnotatedString {
            append(game.sequence)
            // Add the green color to the correct part
            addStyle(
                style = SpanStyle(Color.Green),
                start = 0,
                end = 3*(game.error - 1)
            )
            // Add the red color to the wrong part
            addStyle(
                style = SpanStyle(Color.Red),
                start = 3*(game.error - 1),
                end = game.sequence.length
            )
        }

        // Print the sequence of this game
        Text(
            text = resultString,
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .fillMaxWidth()
                .padding(8.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(Modifier, Game(counter = 8, sequence = "R, G, B, Y, M, R, B, B", error = 6))
}