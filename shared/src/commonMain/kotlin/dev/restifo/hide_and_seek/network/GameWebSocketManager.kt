package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.game.GameState
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Manages WebSocket connections for game updates.
 */
open class GameWebSocketManager(
    private val webSocketManager: WebSocketManager = WebSocketManager.getInstance()
) {
    private val gameState = GameState.getInstance()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var pingJob: Job? = null

    init {
        // Listen for WebSocket messages
        scope.launch {
            webSocketManager.messageFlow.collect { message ->
                try {
                    handleWebSocketMessage(message)
                } catch (e: Exception) {
                    gameState.setError("Error processing message: ${e.message}")
                }
            }
        }
    }

    /**
     * Connects to the game WebSocket.
     *
     * @param websocketUrl The URL of the WebSocket server.
     * @param token The authentication token for the WebSocket connection.
     */
    open fun connect(websocketUrl: String, token: String? = null) {
        webSocketManager.connect(websocketUrl, token)
        gameState.setConnected(true)

        // Start ping job to keep connection alive
        pingJob?.cancel()
        pingJob = scope.launch {
            while (isActive) {
                delay(30000) // Send ping every 30 seconds
                sendPing()
            }
        }
    }

    /**
     * Disconnects from the game WebSocket.
     */
    open fun disconnect() {
        pingJob?.cancel()
        webSocketManager.disconnect()
        gameState.setConnected(false)
    }

    /**
     * Handles incoming WebSocket messages.
     */
    private fun handleWebSocketMessage(message: String) {
        val jsonElement = Json.parseToJsonElement(message)
        val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content

        when (type) {
            "player_joined" -> handlePlayerJoined(jsonElement)
            "player_left" -> handlePlayerLeft(jsonElement)
            "game_started" -> handleGameStarted(jsonElement)
            "game_updated" -> handleGameUpdated(jsonElement)
            "error" -> handleError(jsonElement)
            "pong" -> {} // Ignore pong responses
            else -> gameState.setError("Unknown message type: $type")
        }
    }

    /**
     * Handles player_joined events.
     */
    private fun handlePlayerJoined(jsonElement: JsonElement) {
        val player = jsonElement.jsonObject["data"]?.jsonObject?.get("player")?.let {
            Json.decodeFromJsonElement<Player>(it)
        }

        if (player != null) {
            gameState.addPlayer(player)
        }
    }

    /**
     * Handles player_left events.
     */
    private fun handlePlayerLeft(jsonElement: JsonElement) {
        val playerId = jsonElement.jsonObject["data"]?.jsonObject?.get("player_id")?.jsonPrimitive?.content

        if (playerId != null) {
            gameState.removePlayer(playerId)
        }
    }

    /**
     * Handles game_started events.
     */
    private fun handleGameStarted(jsonElement: JsonElement) {
        val startedAt = jsonElement.jsonObject["data"]?.jsonObject?.get("started_at")?.jsonPrimitive?.content

        // Update game status
        val currentGame = gameState.gameFlow.value
        if (currentGame != null && startedAt != null) {
            gameState.updateGame(currentGame.copy(
                status = "active",
                started_at = startedAt
            ))
        }
    }

    /**
     * Handles game_updated events.
     */
    private fun handleGameUpdated(jsonElement: JsonElement) {
        val game = jsonElement.jsonObject["data"]?.jsonObject?.get("game")?.let {
            Json.decodeFromJsonElement<Game>(it)
        }

        if (game != null) {
            gameState.updateGame(game)
        }
    }

    /**
     * Handles error events.
     */
    private fun handleError(jsonElement: JsonElement) {
        val code = jsonElement.jsonObject["data"]?.jsonObject?.get("code")?.jsonPrimitive?.content
        val message = jsonElement.jsonObject["data"]?.jsonObject?.get("message")?.jsonPrimitive?.content

        gameState.setError("Error ($code): $message")
    }

    /**
     * Sends a ping message to keep the connection alive.
     */
    private fun sendPing() {
        val pingMessage = WebSocketMessage(type = "ping", data = emptyMap())
        webSocketManager.sendObject(pingMessage)
    }

    /**
     * Sends a leave_game message.
     */
    open fun sendLeaveGame() {
        val leaveMessage = WebSocketMessage(type = "leave_game", data = emptyMap())
        webSocketManager.sendObject(leaveMessage)
    }

    companion object {
        private var instance: GameWebSocketManager? = null

        fun getInstance(): GameWebSocketManager {
            if (instance == null) {
                instance = GameWebSocketManager()
            }
            return instance!!
        }
    }
}

/**
 * WebSocket message structure.
 */
@Serializable
data class WebSocketMessage(
    val type: String,
    val data: Map<String, JsonElement>
)
