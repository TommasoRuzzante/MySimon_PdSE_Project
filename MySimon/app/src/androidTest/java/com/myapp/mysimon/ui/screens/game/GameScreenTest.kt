package com.myapp.mysimon.ui.screens.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreen_displaysSequenceAndGameControls() {
        composeTestRule.setContent {
            GameScreen(
                gameState = GameState.USER_TURN,
                text = "R, G, B",
                activeButtonIndex = 1,
                onColoredButtonClick = {},
                onStartButtonClick = {},
                onPauseButtonClick = {},
                onEndgameButtonClick = {}
            )
        }

        composeTestRule.onNodeWithText("R, G, B").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pause").assertIsDisplayed()
        composeTestRule.onNodeWithText("End Game").assertIsDisplayed()
    }

    @Test
    fun startButton_clickTriggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            GameScreen(
                gameState = GameState.STARTING,
                text = "",
                activeButtonIndex = -1,
                onColoredButtonClick = {},
                onStartButtonClick = { clicked = true },
                onPauseButtonClick = {},
                onEndgameButtonClick = {}
            )
        }

        composeTestRule.onNodeWithText("Start Game").performClick()

        assertTrue(clicked)
    }

    @Test
    fun gameOverState_showsGameOverMessage() {
        composeTestRule.setContent {
            GameScreen(
                gameState = GameState.GAME_OVER,
                text = "R, G",
                activeButtonIndex = -1,
                onColoredButtonClick = {},
                onStartButtonClick = {},
                onPauseButtonClick = {},
                onEndgameButtonClick = {}
            )
        }

        composeTestRule.onNodeWithText("The game ended").assertIsDisplayed()
    }
}
