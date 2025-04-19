package dev.restifo.hide_and_seek.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject

/**
 * A Google Maps component for Android.
 */
@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoomLevel: Float,
    geoJsonData: String?,
    onMapLoaded: () -> Unit,
    onZoomChanged: (Float) -> Unit
) {
    val singapore = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, zoomLevel)
    }
    
    var mapLoaded by remember { mutableStateOf(false) }
    
    // Update camera position when coordinates change
    LaunchedEffect(latitude, longitude) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), zoomLevel)
    }
    
    // Notify when map is loaded
    LaunchedEffect(mapLoaded) {
        if (mapLoaded) {
            onMapLoaded()
        }
    }
    
    // Update zoom level when camera position changes
    LaunchedEffect(cameraPositionState.position) {
        if (mapLoaded && cameraPositionState.position.zoom != zoomLevel) {
            onZoomChanged(cameraPositionState.position.zoom)
        }
    }
    
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        )
    }
    
    val mapProperties = remember {
        MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false
        )
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = { mapLoaded = true }
    ) {
        // TODO: Add GeoJSON layer when available
        // This would require additional implementation to parse and display GeoJSON data
    }
}
