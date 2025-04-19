package dev.restifo.hide_and_seek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.restifo.hide_and_seek.network.LocationSearchResult
import dev.restifo.hide_and_seek.ui.components.MapView
import dev.restifo.hide_and_seek.ui.viewmodels.CreateGameViewModel

/**
 * Screen for creating a new game.
 * First step: selecting a game area.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateGameScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { CreateGameViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scaffoldState = rememberScaffoldState()

    // Show error in snackbar if present
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            scaffoldState.snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
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
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Create New Game",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Content based on current state
            val selectedLocation = uiState.selectedLocation
            val locationBoundaries = uiState.locationBoundaries
            if (selectedLocation != null && locationBoundaries != null) {
                // Location confirmation screen
                LocationConfirmationScreen(
                    locationName = selectedLocation.title,
                    locationSubtitle = selectedLocation.subtitle,
                    latitude = uiState.mapCenterLatitude ?: 0.0,
                    longitude = uiState.mapCenterLongitude ?: 0.0,
                    zoomLevel = uiState.mapZoomLevel,
                    geoJsonData = uiState.geoJsonBoundaries,
                    onConfirm = { /* Will be implemented in next step */ },
                    onBack = { viewModel.clearSelectedLocation() },
                    onZoomChanged = { viewModel.updateMapZoom(it) },
                    isLoading = uiState.isLoadingBoundaries
                )
            } else {
                // Location search screen
                LocationSearchScreen(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    searchResults = uiState.searchResults,
                    onLocationSelected = { viewModel.selectLocation(it) },
                    isSearching = uiState.isSearching,
                    onClearSearch = { viewModel.updateSearchQuery("") },
                    onDoneAction = { keyboardController?.hide() }
                )
            }
        }
    }
}

/**
 * Screen for searching and selecting a location.
 */
@Composable
private fun LocationSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<LocationSearchResult>,
    onLocationSelected: (LocationSearchResult) -> Unit,
    isSearching: Boolean,
    onClearSearch: () -> Unit,
    onDoneAction: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Select Game Area",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Enter a city, region, or country name",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search locations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDoneAction() })
        )

        // Loading indicator or results
        Box(modifier = Modifier.weight(1f)) {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                Text(
                    text = "No locations found",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.body1,
                    color = Color.Gray
                )
            } else {
                LazyColumn {
                    items(searchResults) { location ->
                        LocationSearchResultItem(
                            location = location,
                            onClick = { onLocationSelected(location) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item displaying a location search result.
 */
@Composable
private fun LocationSearchResultItem(
    location: LocationSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = location.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = location.subtitle,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}

/**
 * Screen for confirming a selected location.
 */
@Composable
private fun LocationConfirmationScreen(
    locationName: String,
    locationSubtitle: String,
    latitude: Double,
    longitude: Double,
    zoomLevel: Float,
    geoJsonData: String?,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    onZoomChanged: (Float) -> Unit,
    isLoading: Boolean
) {
    var mapLoaded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Confirm Location",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Location info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = locationSubtitle,
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }

        // Map view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && !mapLoaded) {
                CircularProgressIndicator()
            } else {
                MapView(
                    modifier = Modifier.fillMaxSize(),
                    latitude = latitude,
                    longitude = longitude,
                    zoomLevel = zoomLevel,
                    geoJsonData = geoJsonData,
                    onMapLoaded = { mapLoaded = true },
                    onZoomChanged = onZoomChanged
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                enabled = !isLoading
            ) {
                Text("Confirm")
            }
        }
    }
}
