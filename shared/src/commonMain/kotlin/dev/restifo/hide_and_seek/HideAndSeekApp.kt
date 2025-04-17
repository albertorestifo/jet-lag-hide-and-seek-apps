package dev.restifo.hide_and_seek

import dev.restifo.hide_and_seek.config.BuildConfig

/**
 * Main application class for Hide and Seek.
 */
object HideAndSeekApp {
    /**
     * Initialize the application with the specified environment.
     */
    fun initialize(isDevelopment: Boolean) {
        val environment = if (isDevelopment) {
            BuildConfig.Environment.DEVELOPMENT
        } else {
            BuildConfig.Environment.PRODUCTION
        }
        
        BuildConfig.setEnvironment(environment)
    }
}
