package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.config.BuildConfig
import dev.restifo.hide_and_seek.network.*
import dev.restifo.hide_and_seek.persistence.GamePersistenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service for game-related operations.
 */
class GameService(
    // Make these properties internal so they can be replaced in tests
    internal var apiService: HideAndSeekApiService = HideAndSeekApiService.getInstance(),
    internal var webSocketManager: GameWebSocketManager = GameWebSocketManager.getInstance(),
    private val gameState: GameState = GameState.getInstance(),
    private val persistenceManager: GamePersistenceManager = GamePersistenceManager.getInstance()
) {
    // Track the current connection token
    private var currentToken: String? = null

    /**
     * Checks if a game exists with the given code.
     */
    suspend fun checkGameExists(gameCode: String): Boolean = withContext(Dispatchers.Default) {
        try {
            val response = apiService.checkGameExists(gameCode)
            return@withContext response.exists
        } catch (e: Exception) {
            gameState.setError("Error checking game: ${e.message}")
            return@withContext false
        }
    }

    /**
     * Joins a game with the given code and player name.
     */
    suspend fun joinGame(gameCode: String, playerName: String): Boolean = withContext(Dispatchers.Default) {
        try {
            val response = apiService.joinGame(gameCode, playerName)

            // Save the token
            currentToken = response.token

            // Connect to WebSocket with token
            webSocketManager.connect(response.websocket_url, response.token)

            // Persist the connection credentials
            persistenceManager.saveConnectionCredentials(
                gameId = response.game_id,
                token = response.token
            )

            // Game state will be received via WebSocket

            return@withContext true
        } catch (e: Exception) {
            gameState.setError("Error joining game: ${e.message}")
            return@withContext false
        }
    }

    /**
     * Leaves the current game.
     */
    fun leaveGame() {
        try {
            // Send leave_game message
            webSocketManager.sendLeaveGame()

            // Disconnect from WebSocket
            webSocketManager.disconnect()

            // Clear game state
            gameState.clear()

            // Clear persistence
            persistenceManager.clearConnectionCredentials()
            currentToken = null
        } catch (e: Exception) {
            gameState.setError("Error leaving game: ${e.message}")
        }
    }

    /**
     * Restores the connection from persistence if available.
     * Returns true if connection was restored, false otherwise.
     */
    suspend fun restoreConnection(): Boolean = withContext(Dispatchers.Default) {
        val credentials = persistenceManager.loadConnectionCredentials() ?: return@withContext false

        try {
            // Save current token for reconnection
            currentToken = credentials.token

            // Connect to WebSocket with token
            // The WebSocket URL is constructed from the game ID
            val websocketUrl = "${BuildConfig.apiBaseUrl}/api/games/${credentials.gameId}/ws"
            webSocketManager.connect(websocketUrl, credentials.token)

            // Game state will be received via WebSocket
            return@withContext true
        } catch (e: Exception) {
            gameState.setError("Error restoring connection: ${e.message}")
            persistenceManager.clearConnectionCredentials()
            return@withContext false
        }
    }

    companion object {
        private var instance: GameService? = null

        fun getInstance(): GameService {
            if (instance == null) {
                instance = GameService()
            }
            return instance!!
        }
    }
}
