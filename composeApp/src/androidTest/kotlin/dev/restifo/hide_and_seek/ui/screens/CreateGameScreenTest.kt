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
    fun createGameScreen_displaysInitialElements() {
        // Arrange
        composeTestRule.setContent {
            CreateGameScreen(
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Create New Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select Game Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a city, region, or country name").assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).assertIsDisplayed() // Search field
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
    fun createGameScreen_searchFieldInteraction() {
        // Arrange
        composeTestRule.setContent {
            CreateGameScreen(
                onBack = {}
            )
        }

        // Act - enter text in search field
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Madrid")

        // Assert - clear button should appear
        composeTestRule.onNodeWithContentDescription("Clear").assertIsDisplayed()

        // Act - clear the search
        composeTestRule.onNodeWithContentDescription("Clear").performClick()

        // Assert - search field should be empty
        composeTestRule.onNode(hasSetTextAction()).assertTextEquals("")
    }
}
