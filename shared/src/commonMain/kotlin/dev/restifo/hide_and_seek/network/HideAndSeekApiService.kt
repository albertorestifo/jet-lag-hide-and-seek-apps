package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Service for interacting with the Hide and Seek game API.
 */
class HideAndSeekApiService {
    private val apiClient = ApiClient.getInstance()

    /**
     * Check if a game exists with the given code.
     */
    suspend fun checkGameExists(gameCode: String): CheckGameExistsResponse {
        return apiClient.httpClient.get {
            url("${BuildConfig.apiBaseUrl}/api/games/check/$gameCode")
        }.body()
    }

    /**
     * Join an existing game.
     */
    suspend fun joinGame(gameCode: String, playerName: String): JoinGameResponse {
        return apiClient.httpClient.post {
            url("${BuildConfig.apiBaseUrl}/api/games/join")
            contentType(ContentType.Application.Json)
            setBody(JoinGameRequest(game_code = gameCode, player_name = playerName))
        }.body()
    }

    /**
     * Create a new game (placeholder for now).
     */
    suspend fun createGame(request: CreateGameRequest): CreateGameResponse {
        return apiClient.httpClient.post {
            url("${BuildConfig.apiBaseUrl}/api/games")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object {
        // Singleton instance
        private var instance: HideAndSeekApiService? = null

        /**
         * Get the singleton instance of the API service.
         */
        fun getInstance(): HideAndSeekApiService {
            if (instance == null) {
                instance = HideAndSeekApiService()
            }
            return instance!!
        }
    }
}

/**
 * Response from checking if a game exists.
 */
@Serializable
data class CheckGameExistsResponse(
    val exists: Boolean,
    val game_id: String? = null
)

/**
 * Request to join a game.
 */
@Serializable
data class JoinGameRequest(
    val game_code: String,
    val player_name: String
)

/**
 * Response from joining a game.
 */
@Serializable
data class JoinGameResponse(
    val game_id: String,
    val player_id: String,
    val websocket_url: String,
    val game: Game
)

/**
 * A game.
 */
@Serializable
data class Game(
    val id: String,
    val code: String,
    val status: String,
    val players: List<Player>,
    val location: Location,
    val settings: GameSettings,
    val created_at: String,
    val started_at: String? = null
)

/**
 * A player in the game.
 */
@Serializable
data class Player(
    val id: String,
    val name: String,
    val is_creator: Boolean = false
)

/**
 * A location in the game.
 */
@Serializable
data class Location(
    val name: String,
    val coordinates: List<Double>,
    val type: String? = null,
    val osm_id: String? = null,
    val osm_type: String? = null,
    val bounding_box: List<Double>? = null
)

/**
 * Settings for a game.
 */
@Serializable
data class GameSettings(
    val units: String,
    val hiding_zones: List<String>,
    val hiding_zone_size: Int,
    val game_duration: Int,
    val day_start_time: String,
    val day_end_time: String
)

/**
 * Request to create a new game.
 */
@Serializable
data class CreateGameRequest(
    val location: Location,
    val settings: GameSettings,
    val creator: Creator
)

/**
 * Creator of a game.
 */
@Serializable
data class Creator(
    val name: String
)

/**
 * Response from creating a game.
 */
@Serializable
data class CreateGameResponse(
    val game_id: String,
    val game_code: String,
    val websocket_url: String
)
