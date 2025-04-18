package dev.restifo.hide_and_seek.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appNavigation_startsWithMainScreen() {
        // Arrange & Act
        composeTestRule.setContent {
            AppNavigation()
        }

        // Assert
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
        composeTestRule.onNodeWithText("Game Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Join Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create New Game").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesToPlayerNameScreenWhenGameCodeIsValid() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert
        composeTestRule.onNodeWithText("Join Game: ABC123").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Name").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesToCreateGameScreenWhenCreateGameButtonIsClicked() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act
        composeTestRule.onNodeWithText("Create New Game").performClick()

        // Assert
        composeTestRule.onNodeWithText("Create New Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Game Creation Wizard").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesToGameLobbyScreenWhenPlayerNameIsProvided() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act - Navigate to player name screen
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Act - Enter player name and join game
        composeTestRule.onNodeWithText("Enter your name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Assert
        composeTestRule.onNodeWithText("Welcome, John Doe!").assertIsDisplayed()
        composeTestRule.onNodeWithText("You've joined game ABC123.").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesBackToMainScreenFromPlayerNameScreen() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act - Navigate to player name screen
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Act - Navigate back to main screen
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesBackToMainScreenFromCreateGameScreen() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act - Navigate to create game screen
        composeTestRule.onNodeWithText("Create New Game").performClick()

        // Act - Navigate back to main screen
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
    }

    @Test
    fun appNavigation_navigatesBackToMainScreenFromGameLobbyScreen() {
        // Arrange
        composeTestRule.setContent {
            AppNavigation()
        }

        // Act - Navigate to player name screen
        composeTestRule.onNodeWithText("Enter 6-character code").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Act - Enter player name and join game
        composeTestRule.onNodeWithText("Enter your name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Join Game").performClick()

        // Act - Navigate back to main screen
        composeTestRule.onNodeWithText("Leave Game").performClick()

        // Assert
        composeTestRule.onNodeWithText("JetLag").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hide & Seek").assertIsDisplayed()
    }
}
