package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.network.*
import dev.restifo.hide_and_seek.persistence.ConnectionCredentials
import dev.restifo.hide_and_seek.persistence.GamePersistenceManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameServiceTest {

    @Test
    fun testGameServiceBasics() {
        // This test verifies that the GameService class can be instantiated
        // and basic operations work
        val mockWebSocketManager = MockGameWebSocketManager()
        val mockPersistenceManager = MockGamePersistenceManager()

        // Create a GameService instance with mocks
        val gameService = GameService(
            apiService = MockHideAndSeekApiService(),
            webSocketManager = mockWebSocketManager,
            gameState = GameState.getInstance(),
            persistenceManager = mockPersistenceManager
        )

        // Verify the service can be used
        assertFalse(mockWebSocketManager.disconnectCalled)
    }

    @Test
    fun testLeaveGame() {
        // Given
        val gameState = GameState.getInstance()
        val mockWebSocketManager = MockGameWebSocketManager()
        val mockPersistenceManager = MockGamePersistenceManager()

        val gameService = GameService(
            apiService = MockHideAndSeekApiService(),
            webSocketManager = mockWebSocketManager,
            gameState = gameState,
            persistenceManager = mockPersistenceManager
        )

        // Setup initial state
        val player = Player(id = "player-1", name = "John", is_creator = true)
        gameState.addPlayer(player)
        gameState.setCurrentPlayerId("player-1")
        gameState.setConnected(true)

        // Setup is already done in the constructor

        // When
        gameService.leaveGame()

        // Then
        assertTrue(mockWebSocketManager.leaveGameCalled)
        assertTrue(mockWebSocketManager.disconnectCalled)
        assertTrue(gameState.playersFlow.value.isEmpty())
        assertFalse(gameState.isConnectedFlow.value)
        assertTrue(mockPersistenceManager.clearConnectionCredentialsCalled)
    }

/**
 * Mock implementation of GameWebSocketManager for testing.
 */
class MockGameWebSocketManager : GameWebSocketManager() {
    var lastConnectedUrl: String? = null
    var disconnectCalled: Boolean = false
    var leaveGameCalled: Boolean = false

    override fun connect(websocketUrl: String, token: String?) {
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
 * Mock implementation of GamePersistenceManager for testing.
 */
class MockGamePersistenceManager : GamePersistenceManager {
    var saveConnectionCredentialsCalled = false
    var loadConnectionCredentialsCalled = false
    var clearConnectionCredentialsCalled = false
    var credentials: ConnectionCredentials? = null

    override fun saveConnectionCredentials(gameId: String, token: String) {
        saveConnectionCredentialsCalled = true
        credentials = ConnectionCredentials(
            gameId = gameId,
            token = token
        )
    }

    override fun loadConnectionCredentials(): ConnectionCredentials? {
        loadConnectionCredentialsCalled = true
        return credentials
    }

    override fun clearConnectionCredentials() {
        clearConnectionCredentialsCalled = true
        credentials = null
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
