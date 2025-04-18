package dev.restifo.hide_and_seek.persistence

import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.Player
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of GamePersistenceManager using NSUserDefaults.
 */
class IosGamePersistenceManager : GamePersistenceManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val json = Json { ignoreUnknownKeys = true }

    override fun saveConnectionCredentials(gameId: String, token: String) {
        userDefaults.setObject(gameId, KEY_GAME_ID)
        userDefaults.setObject(token, KEY_TOKEN)
        userDefaults.synchronize()
    }

    override fun loadConnectionCredentials(): ConnectionCredentials? {
        // Check if we have saved credentials
        if (userDefaults.objectForKey(KEY_GAME_ID) == null || userDefaults.objectForKey(KEY_TOKEN) == null) {
            return null
        }

        // Load credentials
        val gameId = userDefaults.stringForKey(KEY_GAME_ID) ?: return null
        val token = userDefaults.stringForKey(KEY_TOKEN) ?: return null

        return ConnectionCredentials(
            gameId = gameId,
            token = token
        )
    }

    override fun clearConnectionCredentials() {
        userDefaults.removeObjectForKey(KEY_GAME_ID)
        userDefaults.removeObjectForKey(KEY_TOKEN)
        userDefaults.synchronize()
    }

    companion object {
        private const val KEY_GAME_ID = "game_id"
        private const val KEY_TOKEN = "token"
    }
}

/**
 * Creates a platform-specific instance of the GamePersistenceManager.
 */
actual fun createPlatformInstance(): GamePersistenceManager {
    return IosGamePersistenceManager()
}
