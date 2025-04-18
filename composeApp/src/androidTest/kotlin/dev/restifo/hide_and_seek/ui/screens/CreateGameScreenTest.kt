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
class CreateGameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createGameScreen_displaysAllElements() {
        // Arrange
        composeTestRule.setContent {
            CreateGameScreen(
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Create New Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Game Creation Wizard").assertIsDisplayed()
        composeTestRule.onNodeWithText("This screen will be implemented in a future update.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go Back").assertIsDisplayed()
    }

    @Test
    fun createGameScreen_callsOnBackWhenBackButtonIsClicked() {
        // Arrange
        var backCalled = false

        composeTestRule.setContent {
            CreateGameScreen(
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
    fun createGameScreen_callsOnBackWhenGoBackButtonIsClicked() {
        // Arrange
        var backCalled = false

        composeTestRule.setContent {
            CreateGameScreen(
                onBack = {
                    backCalled = true
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Go Back").performClick()

        // Assert
        assert(backCalled) { "onBack should be called" }
    }
}
