package dev.restifo.hide_and_seek.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.darwin.NSObject
import platform.objc.sel_registerName

/**
 * A MapKit map component for iOS.
 */
@OptIn(ExperimentalForeignApi::class)
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
    // Convert zoom level to span (approximate conversion)
    val span = 10000.0 / zoomLevel
    
    // Create map delegate
    val mapDelegate = remember { MapViewDelegate(onZoomChanged) }
    
    UIKitView(
        modifier = modifier,
        factory = {
            val mapView = MKMapView().apply {
                setDelegate(mapDelegate)
                
                // Set initial region
                val coordinate = CLLocationCoordinate2DMake(latitude, longitude)
                val region = MKCoordinateRegionMakeWithDistance(coordinate, span, span)
                setRegion(region, true)
            }
            
            // Notify that map is loaded
            onMapLoaded()
            
            mapView
        },
        update = { mapView ->
            // Update map region when coordinates change
            val coordinate = CLLocationCoordinate2DMake(latitude, longitude)
            val region = MKCoordinateRegionMakeWithDistance(coordinate, span, span)
            mapView.setRegion(region, true)
            
            // TODO: Add overlay for GeoJSON data when available
        }
    )
}

/**
 * Delegate for MKMapView to handle map events.
 */
@OptIn(ExperimentalForeignApi::class)
private class MapViewDelegate(
    private val onZoomChanged: (Float) -> Unit
) : NSObject(), MKMapViewDelegateProtocol {
    
    // Called when the region displayed by the map view changes
    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        // Calculate zoom level from region span (approximate conversion)
        val span = mapView.region.span.longitudeDelta
        val zoomLevel = (10000.0 / span).toFloat().coerceIn(1f, 20f)
        onZoomChanged(zoomLevel)
    }
}
