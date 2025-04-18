package dev.restifo.hide_and_seek.persistence

import android.content.Context
import android.content.SharedPreferences
import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.Player
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Android implementation of GamePersistenceManager using SharedPreferences.
 */
class AndroidGamePersistenceManager(private val context: Context) : GamePersistenceManager {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val json = Json { ignoreUnknownKeys = true }

    override fun saveConnectionCredentials(gameId: String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_GAME_ID, gameId)
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    override fun loadConnectionCredentials(): ConnectionCredentials? {
        // Check if we have saved credentials
        if (!sharedPreferences.contains(KEY_GAME_ID) || !sharedPreferences.contains(KEY_TOKEN)) {
            return null
        }

        // Load credentials
        val gameId = sharedPreferences.getString(KEY_GAME_ID, null) ?: return null
        val token = sharedPreferences.getString(KEY_TOKEN, null) ?: return null

        return ConnectionCredentials(
            gameId = gameId,
            token = token
        )
    }

    override fun clearConnectionCredentials() {
        sharedPreferences.edit()
            .remove(KEY_GAME_ID)
            .remove(KEY_TOKEN)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "hide_and_seek_game_state"
        private const val KEY_GAME_ID = "game_id"
        private const val KEY_TOKEN = "token"
    }
}

/**
 * Creates a platform-specific instance of the GamePersistenceManager.
 */
actual fun createPlatformInstance(): GamePersistenceManager {
    // Get the application context from the Android context provider
    val context = AndroidContextProvider.getApplicationContext()
    return AndroidGamePersistenceManager(context)
}

/**
 * Provider for Android context.
 * This needs to be initialized in the application class.
 */
object AndroidContextProvider {
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getApplicationContext(): Context {
        return applicationContext ?: throw IllegalStateException(
            "AndroidContextProvider not initialized. Call AndroidContextProvider.init() in your Application class."
        )
    }
}
