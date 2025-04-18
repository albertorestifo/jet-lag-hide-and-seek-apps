package dev.restifo.hide_and_seek.ui.navigation

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import dev.restifo.hide_and_seek.network.HideAndSeekApiService
import dev.restifo.hide_and_seek.ui.screens.*
import kotlinx.coroutines.launch

/**
 * Enum representing the different screens in the app.
 */
enum class Screen {
    MAIN,
    PLAYER_NAME,
    CREATE_GAME,
    GAME_LOBBY
}

/**
 * Navigation component for the app.
 * Handles navigation between different screens.
 */
@Composable
fun AppNavigation() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val apiService = remember { HideAndSeekApiService.getInstance() }
    
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var gameCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    when (currentScreen) {
        Screen.MAIN -> {
            MainScreen(
                onJoinGame = { code ->
                    // For now, just navigate to the player name screen
                    // In a real app, we would check if the game exists
                    gameCode = code
                    currentScreen = Screen.PLAYER_NAME
                },
                onCreateGame = {
                    currentScreen = Screen.CREATE_GAME
                },
                isLoading = isLoading,
                snackbarHostState = snackbarHostState
            )
        }
        
        Screen.PLAYER_NAME -> {
            PlayerNameScreen(
                gameCode = gameCode,
                onJoinWithName = { code, name ->
                    // For now, just navigate to the game lobby
                    // In a real app, we would join the game
                    gameCode = code
                    playerName = name
                    currentScreen = Screen.GAME_LOBBY
                },
                onBack = {
                    currentScreen = Screen.MAIN
                },
                isLoading = isLoading,
                snackbarHostState = snackbarHostState
            )
        }
        
        Screen.CREATE_GAME -> {
            CreateGameScreen(
                onBack = {
                    currentScreen = Screen.MAIN
                }
            )
        }
        
        Screen.GAME_LOBBY -> {
            GameLobbyScreen(
                gameCode = gameCode,
                playerName = playerName,
                onBack = {
                    currentScreen = Screen.MAIN
                }
            )
        }
    }
}
