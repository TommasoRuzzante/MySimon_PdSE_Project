package com.myapp.mysimon.ui.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AccountScreen(
    bestScore: Int = 0,
    gamesPlayed: Int = 0
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hi, this is your account screen!")

        Text("Best score: $bestScore")

        Text("Games played: $gamesPlayed")
    }
}

@Composable
@Preview(showBackground = true)
fun AccountScreenPreview() {
    AccountScreen()
}