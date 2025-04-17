package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.plugins.defaultRequest.*
import kotlinx.serialization.Serializable

/**
 * Service for interacting with the game API.
 */
class GameApiService {
    private val apiClient = ApiClient.getInstance()

    /**
     * Create a new game session.
     */
    suspend fun createGame(request: CreateGameRequest): CreateGameResponse {
        return apiClient.httpClient.post {
            url("${BuildConfig.apiBaseUrl}/games")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Join an existing game session.
     */
    suspend fun joinGame(gameId: String, request: JoinGameRequest): JoinGameResponse {
        return apiClient.httpClient.post {
            url("${BuildConfig.apiBaseUrl}/games/$gameId/join")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Get information about a game.
     */
    suspend fun getGame(gameId: String): GameInfo {
        return apiClient.httpClient.get {
            url("${BuildConfig.apiBaseUrl}/games/$gameId")
        }.body()
    }

    companion object {
        // Singleton instance
        private var instance: GameApiService? = null

        /**
         * Get the singleton instance of the game API service.
         */
        fun getInstance(): GameApiService {
            if (instance == null) {
                instance = GameApiService()
            }
            return instance!!
        }
    }
}

/**
 * Request to create a new game.
 */
@Serializable
data class CreateGameRequest(
    val playableArea: String,
    val allowedHidingLocations: List<String>,
    val hidingTime: Int // in seconds
)

/**
 * Response from creating a new game.
 */
@Serializable
data class CreateGameResponse(
    val gameId: String,
    val hostPlayerId: String
)

/**
 * Request to join a game.
 */
@Serializable
data class JoinGameRequest(
    val playerName: String
)

/**
 * Response from joining a game.
 */
@Serializable
data class JoinGameResponse(
    val playerId: String,
    val gameInfo: GameInfo
)

/**
 * Information about a game.
 */
@Serializable
data class GameInfo(
    val gameId: String,
    val hostPlayerId: String,
    val players: List<Player>,
    val playableArea: String,
    val allowedHidingLocations: List<String>,
    val hidingTime: Int,
    val status: GameStatus
)

/**
 * Information about a player.
 */
@Serializable
data class Player(
    val id: String,
    val name: String,
    val role: PlayerRole
)

/**
 * Role of a player in the game.
 */
@Serializable
enum class PlayerRole {
    HIDER,
    SEEKER
}

/**
 * Status of a game.
 */
@Serializable
enum class GameStatus {
    WAITING_FOR_PLAYERS,
    HIDING,
    SEEKING,
    COMPLETED
}
