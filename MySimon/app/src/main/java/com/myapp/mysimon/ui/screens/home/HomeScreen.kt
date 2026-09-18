package com.myapp.mysimon.ui.screens.home

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
import com.myapp.mysimon.R
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.data.User
import com.myapp.mysimon.ui.theme.LightBlueGrey50
import com.myapp.mysimon.ui.theme.OrangeA400

/**
 * The entry screen of the application.
 * Displays a list of previously played games and provides navigation to Account and Detail screens.
 */
@Composable
fun HomeScreen(
    user: User?, // User profile to display personalized greetings
    buttonDetailScreen : (gameId: Int) -> Unit, // Callback to navigate to game details
    buttonAccountScreen : () -> Unit, // Callback to navigate to the account screen
    games: List<Game> // List of game history fetched from the database
) {
    val title = stringResource(R.string.game_title)
    val oldGames = stringResource(R.string.old_games)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title
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

        // Account navigation section
        AccountRow(
            user = user,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onButtonClick = buttonAccountScreen
        )

        // History Section Header
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

        // List of previous game attempts
        PreviousGames(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            buttonDetailScreen = buttonDetailScreen,
            games = games
        )
    }
}

/**
 * Composable that represents the user account shortcut.
 */
@Composable
fun AccountRow(user: User?, modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    val hello = stringResource(R.string.greetings)
    val myAccount = stringResource(R.string.my_account)
    // Display the user's name if set, otherwise fallback to "My Account"
    val displayName = if (!user?.name.isNullOrBlank()) user.name else myAccount

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(2f),
            text = "$hello $displayName!",
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            onClick = onButtonClick
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = myAccount,
                modifier = Modifier.fillMaxSize(),
                tint = OrangeA400
            )
        }
    }
}

/**
 * Displays a scrollable list of historical game results.
 */
@Composable
fun PreviousGames(
    modifier: Modifier = Modifier,
    buttonDetailScreen: (gameId: Int) -> Unit,
    games: List<Game>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(games.size) { index ->
            Row(
                modifier = Modifier
                    .clickable(onClick = { buttonDetailScreen(games[index].id) })
                    .fillMaxWidth()
                    .background(color = LightBlueGrey50, shape = RoundedCornerShape(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Score achieved in this game
                Text(
                    text = games[index].counter.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Sequence visualization: Green for correct steps, Red for the mistake
                val errorSplitIndex = (3 * (games[index].error - 1)).coerceIn(0, games[index].sequence.length)
                val resultString = buildAnnotatedString {
                    append(games[index].sequence)
                    addStyle(
                        style = SpanStyle(Color.Green),
                        start = 0,
                        end = errorSplitIndex
                    )
                    addStyle(
                        style = SpanStyle(Color.Red),
                        start = errorSplitIndex,
                        end = games[index].sequence.length
                    )
                }

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
    HomeScreen(User(name = "Test"), {}, {}, listOf(
        Game(counter = 4, sequence = "A, B, C, D", error = 4),
        Game(counter = 3, sequence = "X, Y, Z", error = 2)
    ))
}
