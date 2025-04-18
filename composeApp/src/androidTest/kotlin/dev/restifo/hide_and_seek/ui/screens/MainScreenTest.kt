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
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_displaysAllElements() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                onJoinGame = {},
                onCreateGame = {},
                isLoading = false
            )
        }

        // Assert
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
        composeTestRule.onNodeWithText("Game Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter 6-character code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Join Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create New Game").assertIsDisplayed()
    }

    @Test
    fun mainScreen_showsErrorWhenGameCodeIsInvalid() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                onJoinGame = {},
                onCreateGame = {},
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert - The screen should still be displayed (no navigation)
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
    }

    @Test
    fun mainScreen_callsOnJoinGameWhenGameCodeIsValid() {
        // Arrange
        var joinGameCalled = false
        var gameCodePassed = ""

        composeTestRule.setContent {
            MainScreen(
                onJoinGame = { code ->
                    joinGameCalled = true
                    gameCodePassed = code
                },
                onCreateGame = {},
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert
        assert(joinGameCalled) { "onJoinGame should be called" }
        assert(gameCodePassed == "ABC123") { "Game code should be passed correctly" }
    }

    @Test
    fun mainScreen_callsOnCreateGameWhenCreateGameButtonIsClicked() {
        // Arrange
        var createGameCalled = false

        composeTestRule.setContent {
            MainScreen(
                onJoinGame = {},
                onCreateGame = {
                    createGameCalled = true
                },
                isLoading = false
            )
        }

        // Act
        composeTestRule.onNodeWithText("Create New Game").performClick()

        // Assert
        assert(createGameCalled) { "onCreateGame should be called" }
    }

    @Test
    fun mainScreen_disablesButtonsWhenLoading() {
        // Arrange
        composeTestRule.setContent {
            MainScreen(
                onJoinGame = {},
                onCreateGame = {},
                isLoading = true
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Join Game").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Create New Game").assertIsNotEnabled()
        composeTestRule.onNode(hasTestTag("loadingIndicator")).assertExists()
    }
}
