package com.myapp.mysimon

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.ui.screens.home.HomeScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysTitleAndHistory() {
        composeTestRule.setContent {
            HomeScreen(
                buttonDetailScreen = {},
                buttonAccountScreen = {},
                games = listOf(
                    Game(id = 1, counter = 3, sequence = "R, G, Y", error = 2)
                )
            )
        }

        composeTestRule.onNodeWithText("VersusSimon Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Old games").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun accountButton_triggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            HomeScreen(
                buttonDetailScreen = {},
                buttonAccountScreen = { clicked = true },
                games = emptyList()
            )
        }

        composeTestRule.onNodeWithContentDescription("My Account").performClick()

        assertTrue(clicked)
    }
}
