package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.GameSettings
import dev.restifo.hide_and_seek.network.Location
import dev.restifo.hide_and_seek.network.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameStateTest {
    
    @Test
    fun testUpdateGame() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        val player = Player(id = "player-1", name = "John", is_creator = true)
        val game = Game(
            id = "game-123",
            code = "ABC123",
            status = "waiting",
            players = listOf(player),
            location = Location(
                name = "Central Park",
                coordinates = listOf(40.7812, -73.9665)
            ),
            settings = GameSettings(
                units = "metric",
                hiding_zones = listOf("zone1", "zone2"),
                hiding_zone_size = 100,
                game_duration = 3600,
                day_start_time = "08:00",
                day_end_time = "20:00"
            ),
            created_at = "2023-01-01T12:00:00Z"
        )
        
        // When
        gameState.updateGame(game)
        
        // Then
        assertEquals(game, gameState.gameFlow.value)
        assertEquals(1, gameState.playersFlow.value.size)
        assertEquals("John", gameState.playersFlow.value[0].name)
    }
    
    @Test
    fun testAddPlayer() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        val player1 = Player(id = "player-1", name = "John", is_creator = true)
        val player2 = Player(id = "player-2", name = "Jane", is_creator = false)
        
        // When
        gameState.addPlayer(player1)
        
        // Then
        assertEquals(1, gameState.playersFlow.value.size)
        assertEquals("John", gameState.playersFlow.value[0].name)
        
        // When adding another player
        gameState.addPlayer(player2)
        
        // Then
        assertEquals(2, gameState.playersFlow.value.size)
        assertEquals("Jane", gameState.playersFlow.value[1].name)
        
        // When adding a duplicate player
        gameState.addPlayer(player1)
        
        // Then - should not add duplicate
        assertEquals(2, gameState.playersFlow.value.size)
    }
    
    @Test
    fun testRemovePlayer() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        val player1 = Player(id = "player-1", name = "John", is_creator = true)
        val player2 = Player(id = "player-2", name = "Jane", is_creator = false)
        
        gameState.addPlayer(player1)
        gameState.addPlayer(player2)
        
        // When
        gameState.removePlayer("player-2")
        
        // Then
        assertEquals(1, gameState.playersFlow.value.size)
        assertEquals("John", gameState.playersFlow.value[0].name)
    }
    
    @Test
    fun testSetCurrentPlayerId() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        // When
        gameState.setCurrentPlayerId("player-1")
        
        // Then
        assertEquals("player-1", gameState.currentPlayerIdFlow.value)
    }
    
    @Test
    fun testSetError() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        // When
        gameState.setError("Test error")
        
        // Then
        assertEquals("Test error", gameState.errorFlow.value)
        
        // When clearing error
        gameState.setError(null)
        
        // Then
        assertNull(gameState.errorFlow.value)
    }
    
    @Test
    fun testSetConnected() {
        // Given
        val gameState = GameState.getInstance()
        gameState.clear() // Reset state
        
        // When
        gameState.setConnected(true)
        
        // Then
        assertTrue(gameState.isConnectedFlow.value)
        
        // When
        gameState.setConnected(false)
        
        // Then
        assertFalse(gameState.isConnectedFlow.value)
    }
    
    @Test
    fun testClear() {
        // Given
        val gameState = GameState.getInstance()
        
        val player = Player(id = "player-1", name = "John", is_creator = true)
        gameState.addPlayer(player)
        gameState.setCurrentPlayerId("player-1")
        gameState.setError("Test error")
        gameState.setConnected(true)
        
        // When
        gameState.clear()
        
        // Then
        assertNull(gameState.gameFlow.value)
        assertTrue(gameState.playersFlow.value.isEmpty())
        assertNull(gameState.currentPlayerIdFlow.value)
        assertNull(gameState.errorFlow.value)
        assertFalse(gameState.isConnectedFlow.value)
    }
}
