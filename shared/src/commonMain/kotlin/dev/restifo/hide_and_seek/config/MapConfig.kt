package dev.restifo.hide_and_seek.config

/**
 * Configuration for map-related functionality.
 */
object MapConfig {
    /**
     * Environment variable name for the MapTiler API key.
     */
    const val ENV_MAPTILER_API_KEY = "MAPTILER_API_KEY"

    /**
     * The MapTiler API key.
     * This should be set by the platform-specific code during app initialization.
     */
    var apiKey: String = ""
        private set

    /**
     * Sets the MapTiler API key.
     * This should be called during app initialization.
     */
    fun setApiKey(key: String) {
        apiKey = key
    }

    /**
     * The MapTiler style URL for the map.
     */
    val styleUrl: String
        get() = "https://api.maptiler.com/maps/streets/style.json?key=$apiKey"
}
