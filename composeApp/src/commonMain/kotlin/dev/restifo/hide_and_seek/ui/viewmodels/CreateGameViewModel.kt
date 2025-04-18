package dev.restifo.hide_and_seek.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.restifo.hide_and_seek.network.GeocodingService
import dev.restifo.hide_and_seek.network.LocationBoundaries
import dev.restifo.hide_and_seek.network.LocationSearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the create game flow.
 */
class CreateGameViewModel : ViewModel() {
    private val geocodingService = GeocodingService.getInstance()
    
    private val _uiState = MutableStateFlow(CreateGameUiState())
    val uiState: StateFlow<CreateGameUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    
    /**
     * Updates the search query and triggers a search after a debounce period.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        
        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(300) // Debounce for 300ms
            searchLocations(query)
        }
    }
    
    /**
     * Searches for locations matching the query.
     */
    private fun searchLocations(query: String) {
        viewModelScope.launch {
            try {
                val results = geocodingService.searchLocations(query)
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        searchResults = emptyList(), 
                        isSearching = false,
                        error = "Error searching locations: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Selects a location and loads its boundaries.
     */
    fun selectLocation(location: LocationSearchResult) {
        _uiState.update { 
            it.copy(
                selectedLocation = location,
                searchQuery = location.title,
                searchResults = emptyList(),
                isLoadingBoundaries = true
            ) 
        }
        
        viewModelScope.launch {
            try {
                val boundaries = geocodingService.getLocationBoundaries(location.id)
                _uiState.update { it.copy(locationBoundaries = boundaries, isLoadingBoundaries = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoadingBoundaries = false,
                        error = "Error loading location boundaries: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Clears the selected location and returns to the search screen.
     */
    fun clearSelectedLocation() {
        _uiState.update { 
            it.copy(
                selectedLocation = null,
                locationBoundaries = null
            ) 
        }
    }
    
    /**
     * Clears the current error.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI state for the create game flow.
 */
data class CreateGameUiState(
    val searchQuery: String = "",
    val searchResults: List<LocationSearchResult> = emptyList(),
    val selectedLocation: LocationSearchResult? = null,
    val locationBoundaries: LocationBoundaries? = null,
    val isSearching: Boolean = false,
    val isLoadingBoundaries: Boolean = false,
    val error: String? = null
)
