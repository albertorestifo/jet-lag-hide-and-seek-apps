package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.network.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameServiceTest {

    @Test
    fun testGameServiceStructure() {
        // Given
        val gameService = GameService.getInstance()

        // Then - verify the service is properly initialized
        assertFalse(gameService.apiService == null)
        assertFalse(gameService.webSocketManager == null)
    }

    @Test
    fun testLeaveGame() {
        // Given
        val gameService = GameService.getInstance()
        val gameState = GameState.getInstance()

        // Setup initial state
        val player = Player(id = "player-1", name = "John", is_creator = true)
        gameState.addPlayer(player)
        gameState.setCurrentPlayerId("player-1")
        gameState.setConnected(true)

        val mockWebSocketManager = MockGameWebSocketManager()

        // Replace the real WebSocket manager with our mock
        gameService.webSocketManager = mockWebSocketManager

        // When
        gameService.leaveGame()

        // Then
        assertTrue(mockWebSocketManager.leaveGameCalled)
        assertTrue(mockWebSocketManager.disconnectCalled)
        assertTrue(gameState.playersFlow.value.isEmpty())
        assertFalse(gameState.isConnectedFlow.value)
    }

/**
 * Mock implementation of GameWebSocketManager for testing.
 */
class MockGameWebSocketManager : GameWebSocketManager() {
    var lastConnectedUrl: String? = null
    var disconnectCalled: Boolean = false
    var leaveGameCalled: Boolean = false

    override fun connect(websocketUrl: String) {
        lastConnectedUrl = websocketUrl
        GameState.getInstance().setConnected(true)
    }

    override fun disconnect() {
        disconnectCalled = true
        GameState.getInstance().setConnected(false)
    }

    override fun sendLeaveGame() {
        leaveGameCalled = true
    }
}

/**
 * Mock implementation of HideAndSeekApiService for testing.
 */
class MockHideAndSeekApiService : HideAndSeekApiService {
    var checkGameExistsResponse: CheckGameExistsResponse? = null
    var joinGameResponse: JoinGameResponse? = null
    var lastCheckedGameCode: String? = null
    var lastJoinedGameCode: String? = null
    var lastJoinedPlayerName: String? = null

    override suspend fun checkGameExists(gameCode: String): CheckGameExistsResponse {
        lastCheckedGameCode = gameCode
        return checkGameExistsResponse ?: throw Exception("Mock not configured")
    }

    override suspend fun joinGame(gameCode: String, playerName: String): JoinGameResponse {
        lastJoinedGameCode = gameCode
        lastJoinedPlayerName = playerName
        return joinGameResponse ?: throw Exception("Mock not configured")
    }

    override suspend fun createGame(location: Location, settings: GameSettings, creatorName: String): CreateGameResponse {
        throw NotImplementedError("Not needed for these tests")
    }

    override suspend fun getGame(gameId: String): Game {
        throw NotImplementedError("Not needed for these tests")
    }

    override suspend fun startGame(gameId: String): Game {
        throw NotImplementedError("Not needed for these tests")
    }
}
}
