package com.myapp.mysimon.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.myapp.mysimon.R
import com.myapp.mysimon.ui.theme.OrangeA400

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