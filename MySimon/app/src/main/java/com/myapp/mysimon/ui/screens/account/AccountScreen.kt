package com.myapp.mysimon.ui.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myapp.mysimon.R

/**
 * Screen displaying the user's personal statistics.
 * Shows the best score achieved and the total number of games played.
 */
@Composable
fun AccountScreen(
    bestScore: Int = 0, // Highest sequence count recorded
    gamesPlayed: Int = 0 // Total count of game sessions stored in DB
) {
    val myAccount = stringResource(R.string.my_account)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Personal greeting
        Text("Hi $myAccount")

        // Display user statistics
        Text("Best score: $bestScore")
        Text("Games played: $gamesPlayed")
    }
}

@Composable
@Preview(showBackground = true)
fun AccountScreenPreview() {
    AccountScreen()
}
