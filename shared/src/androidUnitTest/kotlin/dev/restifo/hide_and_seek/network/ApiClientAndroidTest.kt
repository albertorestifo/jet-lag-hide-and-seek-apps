package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiClientAndroidTest {
    
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
        BuildConfig.setEnvironment(BuildConfig.Environment.DEVELOPMENT)
        
        // When
        val apiBaseUrl = BuildConfig.apiBaseUrl
        val webSocketUrl = BuildConfig.webSocketUrl
        
        // Then
        assertEquals("http://localhost:4000", apiBaseUrl)
        assertEquals("ws://localhost:4000/ws", webSocketUrl)
        
        // Reset to avoid affecting other tests
        BuildConfig.setEnvironment(BuildConfig.Environment.DEVELOPMENT)
    }
}
