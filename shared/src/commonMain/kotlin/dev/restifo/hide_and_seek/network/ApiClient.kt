package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API client for making HTTP requests to the backend.
 */
class ApiClient {
    /**
     * The Ktor HTTP client configured for API requests.
     */
    val httpClient = HttpClient {
        // Configure content negotiation with JSON
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        // Configure logging for development
        if (BuildConfig.isDevelopment) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        // Configure default request settings
        install(DefaultRequest) {
            url(BuildConfig.apiBaseUrl)
            contentType(ContentType.Application.Json)
        }

        // Configure timeout
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    companion object {
        // Singleton instance
        private var instance: ApiClient? = null

        /**
         * Get the singleton instance of the API client.
         */
        fun getInstance(): ApiClient {
            if (instance == null) {
                instance = ApiClient()
            }
            return instance!!
        }
    }
}
