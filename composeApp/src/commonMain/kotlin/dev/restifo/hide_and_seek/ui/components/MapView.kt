package dev.restifo.hide_and_seek.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A cross-platform map component using MapTiler.
 */
@Composable
expect fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoomLevel: Float,
    geoJsonData: String?,
    onMapLoaded: () -> Unit,
    onZoomChanged: (Float) -> Unit
)
