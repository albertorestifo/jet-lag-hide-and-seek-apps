package dev.restifo.hide_and_seek.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HideAndSeekApiServiceTest {
    
    @Test
    fun testJoinGameRequestCreation() {
        // Given
        val gameCode = "ABC123"
        val playerName = "John Doe"
        
        // When
        val request = JoinGameRequest(game_code = gameCode, player_name = playerName)
        
        // Then
        assertEquals(gameCode, request.game_code)
        assertEquals(playerName, request.player_name)
    }
    
    @Test
    fun testGameInfoStructure() {
        // Given
        val gameId = "game-123"
        val gameCode = "ABC123"
        val status = "waiting"
        val players = listOf(
            Player(id = "player-1", name = "John", is_creator = true)
        )
        val location = Location(
            name = "Central Park",
            coordinates = listOf(40.7812, -73.9665)
        )
        val settings = GameSettings(
            units = "metric",
            hiding_zones = listOf("zone1", "zone2"),
            hiding_zone_size = 100,
            game_duration = 3600,
            day_start_time = "08:00",
            day_end_time = "20:00"
        )
        
        // When
        val game = Game(
            id = gameId,
            code = gameCode,
            status = status,
            players = players,
            location = location,
            settings = settings,
            created_at = "2023-01-01T12:00:00Z"
        )
        
        // Then
        assertEquals(gameId, game.id)
        assertEquals(gameCode, game.code)
        assertEquals(status, game.status)
        assertEquals(1, game.players.size)
        assertEquals("John", game.players[0].name)
        assertEquals(true, game.players[0].is_creator)
        assertEquals("Central Park", game.location.name)
        assertEquals("metric", game.settings.units)
        assertEquals(2, game.settings.hiding_zones.size)
        assertEquals(100, game.settings.hiding_zone_size)
        assertEquals(3600, game.settings.game_duration)
    }
    
    @Test
    fun testCreateGameRequestCreation() {
        // Given
        val location = Location(
            name = "Central Park",
            coordinates = listOf(40.7812, -73.9665)
        )
        val settings = GameSettings(
            units = "metric",
            hiding_zones = listOf("zone1", "zone2"),
            hiding_zone_size = 100,
            game_duration = 3600,
            day_start_time = "08:00",
            day_end_time = "20:00"
        )
        val creator = Creator(name = "John")
        
        // When
        val request = CreateGameRequest(
            location = location,
            settings = settings,
            creator = creator
        )
        
        // Then
        assertEquals("Central Park", request.location.name)
        assertEquals("metric", request.settings.units)
        assertEquals("John", request.creator.name)
    }
}
