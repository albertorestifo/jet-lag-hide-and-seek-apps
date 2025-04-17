package dev.restifo.hide_and_seek.config

import kotlin.native.ObjCName

/**
 * Build configuration for the application.
 * This class provides environment-specific configurations.
 */
@ObjCName("BuildConfig")
object BuildConfig {
    /**
     * Enum representing the different environments the app can run in.
     */
    enum class Environment {
        DEVELOPMENT,
        PRODUCTION
    }

    /**
     * The current environment the app is running in.
     * This will be set at build time.
     */
    var environment: Environment = Environment.DEVELOPMENT
        private set

    /**
     * Sets the environment for the application.
     * This should only be called during app initialization.
     */
    fun setEnvironment(env: Environment) {
        environment = env
    }

    /**
     * The base URL for the HTTP API.
     */
    val apiBaseUrl: String
        get() = when (environment) {
            Environment.DEVELOPMENT -> "http://localhost:4000"
            Environment.PRODUCTION -> "https://hide-and-seek.restifo.dev"
        }

    /**
     * The WebSocket URL for real-time communication.
     */
    val webSocketUrl: String
        get() = when (environment) {
            Environment.DEVELOPMENT -> "ws://localhost:4000/ws"
            Environment.PRODUCTION -> "wss://hide-and-seek.restifo.dev/ws"
        }

    /**
     * Indicates whether the app is running in development mode.
     */
    val isDevelopment: Boolean
        get() = environment == Environment.DEVELOPMENT

    /**
     * Indicates whether the app is running in production mode.
     */
    val isProduction: Boolean
        get() = environment == Environment.PRODUCTION
}
