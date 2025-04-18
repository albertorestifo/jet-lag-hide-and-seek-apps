package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class GameLobbyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameLobbyScreen_displaysAllElements() {
        // Arrange
        val gameCode = "ABC123"
        val playerName = "John Doe"
        
        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = gameCode,
                playerName = playerName,
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Game: $gameCode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome, $playerName!").assertIsDisplayed()
        composeTestRule.onNodeWithText("You've joined game $gameCode.").assertIsDisplayed()
        composeTestRule.onNodeWithText("The game lobby and gameplay screens will be implemented in a future update.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leave Game").assertIsDisplayed()
    }

    @Test
    fun gameLobbyScreen_callsOnBackWhenBackButtonIsClicked() {
        // Arrange
        var backCalled = false

        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = "ABC123",
                playerName = "John Doe",
                onBack = {
                    backCalled = true
                }
            )
        }

        // Act
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert
        assert(backCalled) { "onBack should be called" }
    }

    @Test
    fun gameLobbyScreen_callsOnBackWhenLeaveGameButtonIsClicked() {
        // Arrange
        var backCalled = false

        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = "ABC123",
                playerName = "John Doe",
                onBack = {
                    backCalled = true
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Leave Game").performClick()

        // Assert
        assert(backCalled) { "onBack should be called" }
    }
}
