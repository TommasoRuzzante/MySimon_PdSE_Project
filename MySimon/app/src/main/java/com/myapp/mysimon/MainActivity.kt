package com.myapp.mysimon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myapp.mysimon.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        // Get the arrays from the first activity to use them in the current activity
        val sequenceList = intent.getStringArrayExtra("sequenceList") ?: Array(0) {""}
        val counterList = intent.getIntArrayExtra("counterList") ?: IntArray(0)

        // Check if the arrays have the same size, and close the activity if not
        if (sequenceList.size != counterList.size) {
            Log.e("EndScreen", "Data mismatch: sequenceList size ${sequenceList.size} does not match counterList size ${counterList.size}")
            this.finish()
            return
        }

        // Set and display the UI content
        setContent {
            MySimonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Insert the floating action button and by default put it in the bottom right corner
                    floatingActionButton = {
                        FabNewGame(onButtonClick = {
                            // The button start the new game activity
                            val intent = Intent(this, GameActivity::class.java)
                            startActivity(intent)
                        })
                    }
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        buttonDetailScreen = {
                            val intent = Intent(this, DetailActivity::class.java)
                            startActivity(intent)
                        },
                        sequenceList = sequenceList,
                        counterList = counterList
                    )
                }
            }
        }
    }
}

// Function of the second screen of the app
// Contain the sequences of the previous games and how many times buttons were clicked in each sequence
// Receive the arrays of the first activity as parameters to display the previous games
@Composable
fun MainScreen(modifier: Modifier = Modifier, buttonDetailScreen : () -> Unit, sequenceList: Array<String>, counterList: IntArray) {
    // String used on this activity
    val title = stringResource(R.string.game_title)
    val oldGames = stringResource(R.string.old_games)

    // The layout of the endgame activity is contained in a column in portrait and landscape too
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // On top of the layout there is a text with the "title" of the screen
        // This text will not scroll up or down with the lazy column
        // The color of the text is changed depending on the current theme of the device
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        //
        Text(
            text = oldGames,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // Under the text, covering the rest of the screen, there is a lazy column
        // The lazy column contains the sequences and it's scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Every game is inserted into a row, containing the number of clicks and the text of the sequence
            items(sequenceList.size) { index ->
                Row(
                    modifier = Modifier
                        .clickable(onClick = { buttonDetailScreen() })
                        .fillMaxWidth()
                        .background(color = LightBlueGrey50, shape = RoundedCornerShape(4.dp)),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Number of buttons pressed in that sequence
                    // The font make the number a little more bigger than the font of the sequence,
                    Text(
                        text = counterList[index].toString(),
                        modifier = Modifier.weight(1f),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Sequence of that game
                    // The sequence is cut to 4 lines to fit the screen
                    Text(
                        text = sequenceList[index],
                        modifier = Modifier.weight(9f),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

@Composable
fun FabNewGame(onButtonClick: () -> Unit) {
    // String of the button
    val newGame = stringResource(R.string.new_game)

    // Implementation of the button
    ExtendedFloatingActionButton(
        onClick = onButtonClick,
        icon = { Icon(Icons.Filled.PlayArrow, newGame) },
        text = { Text(text = newGame) },
        containerColor = OrangeA400
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(Modifier, {}, Array<String>(0) {""}, IntArray(0))
}