package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiClientIosTest {
    
    @Test
    fun testApiClientInitialization() {
        // Given
        val apiClient = ApiClient.getInstance()
        
        // When
        val httpClient = apiClient.httpClient
        
        // Then
        assertNotNull(httpClient)
    }
    
    @Test
    fun testBuildConfigEnvironment() {
        // Given
        BuildConfig.setEnvironment(BuildConfig.Environment.PRODUCTION)
        
        // When
        val apiBaseUrl = BuildConfig.apiBaseUrl
        val webSocketUrl = BuildConfig.webSocketUrl
        
        // Then
        assertEquals("https://hide-and-seek.restifo.dev", apiBaseUrl)
        assertEquals("wss://hide-and-seek.restifo.dev/ws", webSocketUrl)
        
        // Reset to avoid affecting other tests
        BuildConfig.setEnvironment(BuildConfig.Environment.DEVELOPMENT)
    }
}
