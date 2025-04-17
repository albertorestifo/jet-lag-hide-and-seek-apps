package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manager for WebSocket connections to the backend.
 */
class WebSocketManager {
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var connectionJob: Job? = null
    private var session: WebSocketSession? = null

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    /**
     * The WebSocket client.
     */
    private val wsClient = HttpClient {
        install(WebSockets)

        // Configure logging for development
        if (BuildConfig.isDevelopment) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    /**
     * Connect to the WebSocket server.
     */
    fun connect() {
        if (connectionJob != null) return

        connectionJob = scope.launch {
            try {
                wsClient.webSocketSession {
                    url(BuildConfig.webSocketUrl)
                }.also { webSocketSession ->
                    session = webSocketSession

                    // Listen for incoming messages
                    for (frame in webSocketSession.incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                _messageFlow.emit(text)
                            }
                            else -> { /* Ignore other frame types */ }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle connection errors
                println("WebSocket connection error: ${e.message}")
            } finally {
                session = null
                connectionJob = null
            }
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        session = null
    }

    /**
     * Send a message to the WebSocket server.
     */
    fun sendMessage(message: String) {
        scope.launch {
            session?.send(Frame.Text(message))
        }
    }

    /**
     * Send an object to the WebSocket server as JSON.
     */
    inline fun <reified T> sendObject(obj: T) {
        val jsonString = json.encodeToString(obj)
        sendMessage(jsonString)
    }

    /**
     * Parse a JSON message into an object.
     */
    inline fun <reified T> parseMessage(message: String): T {
        return json.decodeFromString(message)
    }

    companion object {
        // Singleton instance
        private var instance: WebSocketManager? = null

        /**
         * Get the singleton instance of the WebSocket manager.
         */
        fun getInstance(): WebSocketManager {
            if (instance == null) {
                instance = WebSocketManager()
            }
            return instance!!
        }
    }
}
