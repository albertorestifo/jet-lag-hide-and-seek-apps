package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.launch

/**
 * Main screen of the app where users can enter a game code or create a new game.
 */
@Composable
fun MainScreen(
    onJoinGame: (String) -> Unit,
    onCreateGame: () -> Unit,
    isLoading: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var gameCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Text(
                    text = "JetLag",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "Hide & Seek",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                // Game code input
                OutlinedTextField(
                    value = gameCode,
                    onValueChange = {
                        // Only allow uppercase letters and numbers, max 6 characters
                        val filteredValue = it.uppercase().filter { char ->
                            char.isLetterOrDigit()
                        }.take(6)
                        gameCode = filteredValue
                        isError = false
                    },
                    label = { Text("Game Code") },
                    placeholder = { Text("Enter 6-character code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Text
                    ),
                    isError = isError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Join game button
                Button(
                    onClick = {
                        if (gameCode.length == 6) {
                            onJoinGame(gameCode)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading
                ) {
                    Text("Join Game")
                }

                // Create game button
                TextButton(
                    onClick = { onCreateGame() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = !isLoading
                ) {
                    Text("Create New Game")
                }
            }

            // Loading indicator
            if (isLoading) {
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
