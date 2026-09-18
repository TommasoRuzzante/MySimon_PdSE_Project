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

/**
 * Screen displaying the full details of a past game session.
 * Shows the final score and the complete sequence, highlighting the mistake.
 */
@Composable
fun DetailScreen(
    game: Game // The specific game data to display
) {
    val details = stringResource(R.string.game_details)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section Title
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

        // Final score (consecutive correct buttons)
        Text(
            text = game.counter.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Detailed view of the sequence with color-coded results
        DetailedSequence(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            sequence = game.sequence,
            error = game.error
        )
    }
}

/**
 * Composable that renders the game sequence.
 * Colors correct steps in Green and the final mistake in Red.
 */
@Composable
fun DetailedSequence(
    modifier: Modifier = Modifier,
    sequence: String,
    error: Int
) {
    // Logic to calculate where the user failed to apply correct colors
    val errorSplitIndex = (3 * (error - 1)).coerceIn(0, sequence.length)
    val resultString = buildAnnotatedString {
        append(sequence)
        // Correct part in Green
        addStyle(
            style = SpanStyle(Color.Green),
            start = 0,
            end = errorSplitIndex
        )
        // Mistake part in Red
        addStyle(
            style = SpanStyle(Color.Red),
            start = errorSplitIndex,
            end = sequence.length
        )
    }

    // Scroll state for long sequences
    val scrollState = rememberScrollState()

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
