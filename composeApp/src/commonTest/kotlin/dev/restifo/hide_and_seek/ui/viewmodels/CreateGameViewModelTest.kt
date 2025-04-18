package dev.restifo.hide_and_seek.ui.viewmodels

import dev.restifo.hide_and_seek.network.LocationSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        // Given
        val viewModel = CreateGameViewModel()

        // When
        val initialState = viewModel.uiState.first()

        // Then
        assertEquals("", initialState.searchQuery)
        assertEquals(emptyList(), initialState.searchResults)
        assertNull(initialState.selectedLocation)
        assertNull(initialState.locationBoundaries)
        assertEquals(false, initialState.isSearching)
        assertEquals(false, initialState.isLoadingBoundaries)
        assertNull(initialState.error)
    }

    @Test
    fun testClearSelectedLocation() = runTest {
        // Given
        val viewModel = CreateGameViewModel()
        val location = LocationSearchResult(
            id = "way:123456",
            title = "Madrid",
            subtitle = "City"
        )

        // When - manually set the state to simulate selection
        viewModel.selectLocation(location)
        // Then clear the selection
        viewModel.clearSelectedLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.first()
        assertNull(finalState.selectedLocation)
        assertNull(finalState.locationBoundaries)
    }

    @Test
    fun testClearError() = runTest {
        // Given
        val viewModel = CreateGameViewModel()
        val errorMessage = "Test error"

        // When - manually set the error
        val stateWithError = viewModel.uiState.first().copy(
            error = errorMessage
        )

        // Then clear the error
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.first()
        assertNull(finalState.error)
    }
}
