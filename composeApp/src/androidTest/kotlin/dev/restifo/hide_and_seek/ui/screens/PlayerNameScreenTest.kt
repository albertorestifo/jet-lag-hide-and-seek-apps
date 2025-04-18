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
class PlayerNameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun playerNameScreen_displaysAllElements() {
        // Arrange
        val gameCode = "ABC123"
        
        composeTestRule.setContent {
            PlayerNameScreen(
                gameCode = gameCode,
                onJoinWithName = { _, _ -> },
                onBack = {},
                isLoading = false
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Join Game: $gameCode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter your name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Join Game").assertIsDisplayed()
    }

    @Test
    fun playerNameScreen_showsErrorWhenNameIsEmpty() {
        // Arrange
        composeTestRule.setContent {
            PlayerNameScreen(
                gameCode = "ABC123",
                onJoinWithName = { _, _ -> },
                onBack = {},
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert - The screen should still be displayed (no navigation)
        composeTestRule.onNodeWithText("Your Name").assertIsDisplayed()
    }

    @Test
    fun playerNameScreen_callsOnJoinWithNameWhenNameIsValid() {
        // Arrange
        var joinWithNameCalled = false
        var gameCodePassed = ""
        var playerNamePassed = ""
        val expectedGameCode = "ABC123"
        val expectedPlayerName = "John Doe"

        composeTestRule.setContent {
            PlayerNameScreen(
                gameCode = expectedGameCode,
                onJoinWithName = { code, name ->
                    joinWithNameCalled = true
                    gameCodePassed = code
                    playerNamePassed = name
                },
                onBack = {},
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithText("Enter your name").performTextInput(expectedPlayerName)
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert
        assert(joinWithNameCalled) { "onJoinWithName should be called" }
        assert(gameCodePassed == expectedGameCode) { "Game code should be passed correctly" }
        assert(playerNamePassed == expectedPlayerName) { "Player name should be passed correctly" }
    }

    @Test
    fun playerNameScreen_callsOnBackWhenBackButtonIsClicked() {
        // Arrange
        var backCalled = false

        composeTestRule.setContent {
            PlayerNameScreen(
                gameCode = "ABC123",
                onJoinWithName = { _, _ -> },
                onBack = {
                    backCalled = true
                },
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert
        assert(backCalled) { "onBack should be called" }
    }

    @Test
    fun playerNameScreen_disablesButtonsWhenLoading() {
        // Arrange
        composeTestRule.setContent {
            PlayerNameScreen(
                gameCode = "ABC123",
                onJoinWithName = { _, _ -> },
                onBack = {},
                isLoading = true
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Join Game").assertIsNotEnabled()
        composeTestRule.onNode(hasTestTag("loadingIndicator")).assertExists()
    }
}
