package dev.restifo.hide_and_seek

/**
 * Initializes the app for iOS.
 * This is called from Swift code.
 */
object IosAppInitializer {
    /**
     * Initialize the app for iOS.
     * @param isDevelopment Whether the app is running in development mode.
     */
    fun doInitialize(isDevelopment: Boolean) {
        HideAndSeekApp.initialize(isDevelopment)
    }
}
