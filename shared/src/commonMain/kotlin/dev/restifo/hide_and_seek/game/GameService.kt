package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service for game-related operations.
 */
class GameService {
    private val apiService = HideAndSeekApiService.getInstance()
    private val webSocketManager = GameWebSocketManager.getInstance()
    private val gameState = GameState.getInstance()
    
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
            
            // Update game state
            gameState.updateGame(response.game)
            gameState.setCurrentPlayerId(response.player_id)
            
            // Connect to WebSocket
            webSocketManager.connect(response.websocket_url)
            
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
        } catch (e: Exception) {
            gameState.setError("Error leaving game: ${e.message}")
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
