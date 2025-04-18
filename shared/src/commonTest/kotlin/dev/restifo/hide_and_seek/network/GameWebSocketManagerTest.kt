package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.game.GameState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GameWebSocketManagerTest {

    @Test
    fun testConnect() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state

        val mockWebSocketManager = MockWebSocketManager()
        val gameWebSocketManager = GameWebSocketManager(mockWebSocketManager)

        // When
        gameWebSocketManager.connect("ws://test.com/ws")

        // Then
        assertEquals("ws://test.com/ws", mockWebSocketManager.lastConnectedUrl)
        assertTrue(gameState.isConnectedFlow.value)
    }

    @Test
    fun testDisconnect() {
        // Given
        val gameState = GameState.getInstance()
        gameState.setConnected(true)

        val mockWebSocketManager = MockWebSocketManager()
        val gameWebSocketManager = GameWebSocketManager(mockWebSocketManager)

        // When
        gameWebSocketManager.disconnect()

        // Then
        assertTrue(mockWebSocketManager.disconnectCalled)
        assertFalse(gameState.isConnectedFlow.value)
    }

    @Test
    fun testSendLeaveGame() {
        // Given
        val mockWebSocketManager = MockWebSocketManager()
        val gameWebSocketManager = GameWebSocketManager(mockWebSocketManager)

        // When
        gameWebSocketManager.sendLeaveGame()

        // Then
        assertEquals("leave_game", mockWebSocketManager.lastMessageType)
    }
}

/**
 * Mock implementation of WebSocketManager for testing.
 */
class MockWebSocketManager : WebSocketManager() {
    var lastConnectedUrl: String? = null
    var disconnectCalled: Boolean = false
    var lastSentMessage: String? = null
    var lastMessageType: String? = null

    override fun connect(websocketUrl: String?, token: String?) {
        lastConnectedUrl = websocketUrl
    }

    override fun disconnect() {
        disconnectCalled = true
    }

    override fun sendMessage(message: String) {
        lastSentMessage = message
        // Try to extract message type
        if (message.contains("\"type\":")) {
            val typeRegex = Regex("\"type\":\\s*\"([^\"]+)\"")
            val matchResult = typeRegex.find(message)
            lastMessageType = matchResult?.groupValues?.get(1)
        }
    }
}
