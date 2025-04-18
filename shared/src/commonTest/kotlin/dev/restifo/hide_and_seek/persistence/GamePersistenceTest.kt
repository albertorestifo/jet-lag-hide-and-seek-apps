package dev.restifo.hide_and_seek.persistence

import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.GameSettings
import dev.restifo.hide_and_seek.network.Location
import dev.restifo.hide_and_seek.network.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test for the ConnectionCredentials data class.
 */
class GamePersistenceTest {

    @Test
    fun testConnectionCredentials() {
        // Given
        val gameId = "game-123"
        val token = "auth-token-xyz"

        // When
        val credentials = ConnectionCredentials(
            gameId = gameId,
            token = token
        )

        // Then
        assertEquals(gameId, credentials.gameId)
        assertEquals(token, credentials.token)
    }
}
