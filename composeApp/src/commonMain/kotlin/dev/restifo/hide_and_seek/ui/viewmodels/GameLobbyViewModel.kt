package dev.restifo.hide_and_seek.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.restifo.hide_and_seek.game.GameService
import dev.restifo.hide_and_seek.game.GameState
import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the game lobby screen.
 */
class GameLobbyViewModel : ViewModel() {
    private val gameService = GameService.getInstance()
    private val gameState = GameState.getInstance()
    
    private val _uiState = MutableStateFlow(GameLobbyUiState())
    val uiState: StateFlow<GameLobbyUiState> = _uiState.asStateFlow()
    
    init {
        // Observe game state
        viewModelScope.launch {
            gameState.gameFlow.collect { game ->
                _uiState.update { it.copy(game = game) }
            }
        }
        
        // Observe players
        viewModelScope.launch {
            gameState.playersFlow.collect { players ->
                _uiState.update { it.copy(players = players) }
            }
        }
        
        // Observe current player ID
        viewModelScope.launch {
            gameState.currentPlayerIdFlow.collect { playerId ->
                _uiState.update { it.copy(currentPlayerId = playerId) }
            }
        }
        
        // Observe connection status
        viewModelScope.launch {
            gameState.isConnectedFlow.collect { isConnected ->
                _uiState.update { it.copy(isConnected = isConnected) }
            }
        }
        
        // Observe errors
        viewModelScope.launch {
            gameState.errorFlow.collect { error ->
                _uiState.update { it.copy(error = error) }
            }
        }
    }
    
    /**
     * Leaves the current game.
     */
    fun leaveGame() {
        gameService.leaveGame()
    }
    
    /**
     * Clears the current error.
     */
    fun clearError() {
        gameState.setError(null)
    }
}

/**
 * UI state for the game lobby screen.
 */
data class GameLobbyUiState(
    val game: Game? = null,
    val players: List<Player> = emptyList(),
    val currentPlayerId: String? = null,
    val isConnected: Boolean = false,
    val error: String? = null
)
