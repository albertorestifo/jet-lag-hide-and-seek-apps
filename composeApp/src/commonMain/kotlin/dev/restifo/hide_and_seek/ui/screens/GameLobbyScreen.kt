package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.restifo.hide_and_seek.network.Player
import dev.restifo.hide_and_seek.ui.viewmodels.GameLobbyViewModel
import kotlinx.coroutines.launch

/**
 * Game lobby screen where players wait for the game to start.
 */
@Composable
fun GameLobbyScreen(
    gameCode: String,
    playerName: String,
    onBack: () -> Unit
) {
    val viewModel = remember { GameLobbyViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Show error toast if there's an error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Handle WebSocket connection status changes
    LaunchedEffect(uiState.isConnected) {
        if (!uiState.isConnected) {
            // Show a generic error toast for WebSocket connection issues
            snackbarHostState.showSnackbar("Lost connection to the game server. Trying to reconnect...")
        }
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        snackbarHost = { SnackbarHost(hostState = it) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Top bar with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = {
                    viewModel.leaveGame()
                    onBack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Game: $gameCode",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = 8.dp)
                )

                // Connection status indicator
                Spacer(modifier = Modifier.weight(1f))

                if (uiState.isConnected) {
                    Text(
                        text = "Connected",
                        color = Color.Green,
                        style = MaterialTheme.typography.caption
                    )
                } else {
                    Text(
                        text = "Disconnected",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            // Game info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Welcome, $playerName!",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Status: ${uiState.game?.status ?: "Waiting"}",
                        style = MaterialTheme.typography.body1
                    )

                    Text(
                        text = "Players: ${uiState.players.size}",
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            // Players list
            Text(
                text = "Players",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.players) { player ->
                    PlayerItem(
                        player = player,
                        isCurrentPlayer = player.id == uiState.currentPlayerId
                    )
                }
            }

            // Leave game button
            Button(
                onClick = {
                    viewModel.leaveGame()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp)
            ) {
                Text("Leave Game")
            }
        }
    }
}

/**
 * Displays a player in the list.
 */
@Composable
fun PlayerItem(
    player: Player,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp,
        backgroundColor = if (isCurrentPlayer) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player icon
            if (player.is_creator) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Game Creator",
                    tint = MaterialTheme.colors.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Player"
                )
            }

            // Player name
            Text(
                text = player.name + if (isCurrentPlayer) " (You)" else "",
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            // Creator label
            if (player.is_creator) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Creator",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}
