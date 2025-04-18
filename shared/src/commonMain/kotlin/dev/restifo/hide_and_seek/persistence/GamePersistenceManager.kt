package dev.restifo.hide_and_seek.persistence

import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.Player

/**
 * Interface for managing game state persistence.
 */
interface GamePersistenceManager {
    /**
     * Saves the connection credentials.
     */
    fun saveConnectionCredentials(
        gameId: String,
        token: String
    )

    /**
     * Loads the saved connection credentials.
     */
    fun loadConnectionCredentials(): ConnectionCredentials?

    /**
     * Clears the saved connection credentials.
     */
    fun clearConnectionCredentials()

    companion object {
        private var instance: GamePersistenceManager? = null

        fun getInstance(): GamePersistenceManager {
            if (instance == null) {
                instance = createPlatformInstance()
            }
            return instance!!
        }
    }
}

/**
 * Creates a platform-specific instance of the GamePersistenceManager.
 * This is implemented in the platform-specific source sets.
 */
expect fun createPlatformInstance(): GamePersistenceManager

/**
 * Data class representing the connection credentials.
 */
data class ConnectionCredentials(
    val gameId: String,
    val token: String
)
