package com.myapp.mysimon.ui.screens.account

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapp.mysimon.R
import com.myapp.mysimon.data.User
import com.myapp.mysimon.ui.theme.OrangeA400

/**
 * Screen displaying the user's personal statistics and profile information.
 * Allows the user to personalize their name and displays a unique alphanumeric tag.
 */
@Composable
fun AccountScreen(
    user: User?, // Current user profile data
    bestScore: Int = 0, // Highest sequence count recorded
    gamesPlayed: Int = 0, // Total count of game sessions stored in DB
    onSaveName: (String) -> Unit // Callback to save a new name
) {
    // Local state for the text field input
    var nameInput by remember(user?.name) { mutableStateOf(user?.name ?: "") }
    
    // Toggle state to switch between viewing and editing the name.
    // If no name is currently saved, it defaults to editing mode.
    var isEditing by remember(user?.name) { mutableStateOf(user?.name.isNullOrBlank()) }
    
    val myAccount = stringResource(R.string.my_account)
    val myName = stringResource(R.string.name)
    val saveName = stringResource(R.string.save_name)
    val editName = stringResource(R.string.edit_name)
    val gameStatistics = stringResource(R.string.game_statistics)
    val userTag = stringResource(R.string.user_tag)
    val gamesPlayedText = stringResource(R.string.games_played)
    val bestScoreText = stringResource(R.string.best_score)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = myAccount,
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Profile Section: Manages the user's name personalization
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEditing) {
                // Editing mode: show the text field and a button to save the name
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(myName) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { 
                        onSaveName(nameInput)
                        isEditing = false // Switch to display mode after saving
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
                ) {
                    Text(saveName)
                }
            } else {
                // Display mode: show the saved name as normal Text and a button to enter editing mode
                Text(
                    text = "$myName: ${user?.name ?: ""}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeA400)
                ) {
                    Text(editName)
                }
            }
        }

        // Unique Tag Section: Shows the alphanumeric identifier for future online features
        if (user != null && user.tag.isNotEmpty()) {
            Text(
                text = "$userTag: ${user.tag}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Section: Displays the cumulative game results for the user
        Text(
            text = gameStatistics,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSystemInDarkTheme()) OrangeA400 else Color.Black
        )
        Text("$gamesPlayedText: $gamesPlayed")
        Text("$bestScoreText: $bestScore")
    }
}

@Composable
@Preview(showBackground = true)
fun AccountScreenPreview() {
    AccountScreen(
        user = User(name = "Mario", tag = "ABC123XY"),
        bestScore = 10,
        gamesPlayed = 5,
        onSaveName = {}
    )
}
