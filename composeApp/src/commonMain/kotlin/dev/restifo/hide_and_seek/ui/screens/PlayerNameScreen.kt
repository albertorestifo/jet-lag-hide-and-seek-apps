package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import dev.restifo.hide_and_seek.game.GameService
import dev.restifo.hide_and_seek.game.GameState
import kotlinx.coroutines.launch

/**
 * Screen for entering player name when joining a game.
 */
@Composable
fun PlayerNameScreen(
    gameCode: String,
    onJoinWithName: (String, String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var playerName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var localIsLoading by remember { mutableStateOf(isLoading) }

    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val coroutineScope = rememberCoroutineScope()
    val gameService = remember { GameService.getInstance() }
    val gameState = remember { GameState.getInstance() }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top bar with back button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Text(
                        text = "Join Game: $gameCode",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Player name input
                OutlinedTextField(
                    value = playerName,
                    onValueChange = {
                        playerName = it
                        isError = false
                    },
                    label = { Text("Your Name") },
                    placeholder = { Text("Enter your name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    isError = isError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Join game button
                Button(
                    onClick = {
                        if (playerName.isNotBlank()) {
                            // Join the game
                            localIsLoading = true
                            coroutineScope.launch {
                                val success = gameService.joinGame(gameCode, playerName)
                                localIsLoading = false

                                if (success) {
                                    // Successfully joined the game and connected to WebSocket
                                    onJoinWithName(gameCode, playerName)
                                } else {
                                    // Error is already set in GameState
                                    // The error will be shown via the snackbar in AppNavigation
                                }
                            }
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !localIsLoading
                ) {
                    Text("Join Game")
                }
            }

            // Loading indicator
            if (localIsLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("loadingIndicator")
                    )
                }
            }
        }
    }
}
