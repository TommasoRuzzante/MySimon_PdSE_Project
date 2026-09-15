package com.myapp.mysimon.ui.screens.detail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapp.mysimon.R
import com.myapp.mysimon.data.*
import com.myapp.mysimon.ui.theme.*

// Function of the detail screen of the app
// Contain the full sequence of the game and the maximum number of consecutive correct clicks
@Composable
fun DetailScreen(
    game: Game // The game we want to display
) {
    // String used on this screen
    val details = stringResource(R.string.game_details)

    // The layout of the endgame screen is contained in a column in portrait and landscape too
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
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
    DetailScreen(Game(counter = 8, sequence = "R, G, B, Y, M, R, B, B", error = 6))
}