package dev.restifo.hide_and_seek.game

import dev.restifo.hide_and_seek.network.Game
import dev.restifo.hide_and_seek.network.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages the state of the current game.
 */
class GameState {
    private val _gameFlow = MutableStateFlow<Game?>(null)
    val gameFlow: StateFlow<Game?> = _gameFlow.asStateFlow()
    
    private val _playersFlow = MutableStateFlow<List<Player>>(emptyList())
    val playersFlow: StateFlow<List<Player>> = _playersFlow.asStateFlow()
    
    private val _currentPlayerIdFlow = MutableStateFlow<String?>(null)
    val currentPlayerIdFlow: StateFlow<String?> = _currentPlayerIdFlow.asStateFlow()
    
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()
    
    private val _isConnectedFlow = MutableStateFlow(false)
    val isConnectedFlow: StateFlow<Boolean> = _isConnectedFlow.asStateFlow()
    
    /**
     * Updates the game state with a new game.
     */
    fun updateGame(game: Game) {
        _gameFlow.update { game }
        _playersFlow.update { game.players }
    }
    
    /**
     * Sets the current player ID.
     */
    fun setCurrentPlayerId(playerId: String) {
        _currentPlayerIdFlow.update { playerId }
    }
    
    /**
     * Adds a player to the game.
     */
    fun addPlayer(player: Player) {
        _playersFlow.update { currentPlayers ->
            // Check if player already exists
            if (currentPlayers.any { it.id == player.id }) {
                currentPlayers
            } else {
                currentPlayers + player
            }
        }
    }
    
    /**
     * Removes a player from the game.
     */
    fun removePlayer(playerId: String) {
        _playersFlow.update { currentPlayers ->
            currentPlayers.filter { it.id != playerId }
        }
    }
    
    /**
     * Sets an error message.
     */
    fun setError(error: String?) {
        _errorFlow.update { error }
    }
    
    /**
     * Sets the connection status.
     */
    fun setConnected(isConnected: Boolean) {
        _isConnectedFlow.update { isConnected }
    }
    
    /**
     * Clears the game state.
     */
    fun clear() {
        _gameFlow.update { null }
        _playersFlow.update { emptyList() }
        _currentPlayerIdFlow.update { null }
        _errorFlow.update { null }
        _isConnectedFlow.update { false }
    }
    
    companion object {
        private var instance: GameState? = null
        
        fun getInstance(): GameState {
            if (instance == null) {
                instance = GameState()
            }
            return instance!!
        }
    }
}
