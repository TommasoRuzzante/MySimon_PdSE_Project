package com.myapp.mysimon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.ui.theme.LightBlueGrey50
import com.myapp.mysimon.ui.theme.OrangeA400

// Function of the first screen of the app
// Contain the sequences of the previous games and the best score of each sequence (consecutive button pressed correctly)
// From this screen you can open the game screen or access the details of a sequence
@Composable
fun MainScreen(
    buttonDetailScreen : (game: Game) -> Unit, // Button function used pass to the detail screen of a specific game
    buttonAccountScreen : () -> Unit, // Button function used pass to the account screen
    games: List<Game> // List of the games
) {
    // Strings used on this screen
    val title = stringResource(R.string.game_title)
    val oldGames = stringResource(R.string.old_games)

    // The layout of the main screen is contained in a column in portrait and landscape too
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // On top of the layout there is a text with the name of the game
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

        // On the right there is the button to open the account screen
        // It's in a row to lock it in the right corner of the screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            AccountButton(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            onButtonClick = buttonAccountScreen
        )}

        // Title of the section containing the old games
        // This text will not scroll and his color change depending on the current theme of the device
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

        // Under the text, covering the rest of the screen, there is the column containing the sequences of previous games
        PreviousGames(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            buttonDetailScreen = buttonDetailScreen,
            games = games
        )
    }
}

// Composable function that define the floating action button used to pass to the game screen
@Composable
fun FabNewGame(onButtonClick: () -> Unit) {
    // String of the button
    val newGame = stringResource(R.string.new_game)

    // Implementation of the button
    // This button is "extended", so it contains an icon and a text
    ExtendedFloatingActionButton(
        onClick = onButtonClick,
        icon = { Icon(Icons.Filled.PlayArrow, newGame) },
        text = { Text(text = newGame) },
        containerColor = OrangeA400
    )
}

// Composable function to open the account detail screen
@Composable
fun AccountButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    // String of the button
    val myAccount = stringResource(R.string.my_account)

    //
    IconButton(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        onClick = onButtonClick
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = myAccount,
            tint = OrangeA400
        )
    }
}

// Composable function used to display the sequences of the previous games
@Composable
fun PreviousGames(
    modifier: Modifier = Modifier,
    buttonDetailScreen: (game: Game) -> Unit,
    games: List<Game>
) {
    // The lazy column contains every sequence and it's scrollable
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Every game is inserted into a row, containing the number of clicks and the text of the sequence
        items(games.size) { index ->
            Row(
                modifier = Modifier
                    .clickable(onClick = { buttonDetailScreen(games[index]) })
                    .fillMaxWidth()
                    .background(color = LightBlueGrey50, shape = RoundedCornerShape(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Number of buttons pressed in that sequence
                // The font make the number a little more bigger than the font of the sequence,
                Text(
                    text = games[index].counter.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Sequence of that game divided in green part and red part
                val errorSplitIndex = (3 * (games[index].error - 1)).coerceIn(0, games[index].sequence.length)
                val resultString = buildAnnotatedString {
                    append(games[index].sequence)
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
                        end = games[index].sequence.length
                    )
                }

                // Sequence of that game
                // The sequence is cut to 2 lines to fit the screen
                Text(
                    text = resultString,
                    modifier = Modifier.weight(9f),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen({}, {}, listOf(
        Game(counter = 4, sequence = "A, B, C, D", error = 4),
        Game(counter = 3, sequence = "X, Y, Z", error = 2)
    ))
}