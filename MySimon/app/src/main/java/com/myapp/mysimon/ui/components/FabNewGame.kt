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

/**
 * A custom Floating Action Button (FAB) used to navigate the user to the game screen.
 * It is an "Extended" FAB, meaning it includes both a descriptive icon and text.
 */
@Composable
fun FabNewGame(
    onButtonClick: () -> Unit // Callback to execute when the FAB is tapped
) {
    // Localization support for the button label
    val newGame = stringResource(R.string.new_game)

    ExtendedFloatingActionButton(
        onClick = onButtonClick,
        icon = { Icon(Icons.Filled.PlayArrow, contentDescription = newGame) },
        text = { Text(text = newGame) },
        containerColor = OrangeA400 // Thematic primary color for actions
    )
}
