package dev.restifo.hide_and_seek.ui.navigation

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import dev.restifo.hide_and_seek.game.GameService
import dev.restifo.hide_and_seek.game.GameState
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
    val gameService = remember { GameService.getInstance() }
    val gameState = remember { GameState.getInstance() }

    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var gameCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Track WebSocket connection status
    val isConnected by gameState.isConnectedFlow.collectAsState()

    // Handle errors from GameState
    LaunchedEffect(Unit) {
        gameState.errorFlow.collect { error ->
            error?.let {
                snackbarHostState.showSnackbar(it)
                gameState.setError(null)
            }
        }
    }

    when (currentScreen) {
        Screen.MAIN -> {
            MainScreen(
                onJoinGame = { code ->
                    // Navigate to the player name screen after checking if the game exists
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
                    // The game has been joined and WebSocket connection established in PlayerNameScreen
                    // Just navigate to the game lobby
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
                    // When leaving the game lobby, we'll go back to the main screen
                    // The GameLobbyScreen already handles disconnecting from the WebSocket
                    currentScreen = Screen.MAIN
                }
            )
        }
    }
}
