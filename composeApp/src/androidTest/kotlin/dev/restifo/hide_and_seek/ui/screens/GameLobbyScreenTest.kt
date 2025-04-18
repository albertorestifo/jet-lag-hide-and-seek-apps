package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.restifo.hide_and_seek.game.GameState
import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.GameSettings
import dev.restifo.hide_and_seek.network.Location
import dev.restifo.hide_and_seek.network.Player
import org.junit.Before
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

    private val gameState = GameState.getInstance()

    @Before
    fun setup() {
        // Reset game state before each test
        gameState.clear()
    }

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

    @Test
    fun gameLobbyScreen_displaysPlayerList() {
        // Arrange
        val gameCode = "ABC123"
        val playerName = "John"

        // Setup game state with players
        val player1 = Player(id = "player-1", name = "John", is_creator = true)
        val player2 = Player(id = "player-2", name = "Jane", is_creator = false)
        gameState.addPlayer(player1)
        gameState.addPlayer(player2)
        gameState.setCurrentPlayerId("player-1")

        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = gameCode,
                playerName = playerName,
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("John").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane").assertIsDisplayed()
    }

    @Test
    fun gameLobbyScreen_displaysConnectionStatus() {
        // Arrange
        val gameCode = "ABC123"
        val playerName = "John"

        // Setup game state with connection status
        gameState.setConnected(true)

        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = gameCode,
                playerName = playerName,
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Connected").assertIsDisplayed()
    }

    @Test
    fun gameLobbyScreen_displaysGameStatus() {
        // Arrange
        val gameCode = "ABC123"
        val playerName = "John"

        // Setup game state with game
        val player = Player(id = "player-1", name = "John", is_creator = true)
        val game = Game(
            id = "game-123",
            code = gameCode,
            status = "waiting",
            players = listOf(player),
            location = Location(
                name = "Central Park",
                coordinates = listOf(40.7812, -73.9665)
            ),
            settings = GameSettings(
                units = "metric",
                hiding_zones = listOf("zone1", "zone2"),
                hiding_zone_size = 100,
                game_duration = 3600,
                day_start_time = "08:00",
                day_end_time = "20:00"
            ),
            created_at = "2023-01-01T12:00:00Z"
        )
        gameState.updateGame(game)

        composeTestRule.setContent {
            GameLobbyScreen(
                gameCode = gameCode,
                playerName = playerName,
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Status: waiting").assertIsDisplayed()
    }
}
