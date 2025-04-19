package dev.restifo.hide_and_seek.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import dev.restifo.hide_and_seek.config.MapConfig
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIColor
import platform.darwin.NSObject

/**
 * A MapTiler/MapLibre component for iOS.
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
    // Create map delegate
    val mapDelegate = remember { MapViewDelegate(onZoomChanged) }

    // Convert GeoJSON string to NSData if available
    val geoJsonNSData = remember(geoJsonData) {
        if (geoJsonData != null) {
            val nsString = NSString.create(string = geoJsonData)
            nsString.dataUsingEncoding(NSUTF8StringEncoding)
        } else {
            null
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            // Create MapLibre map view
            val mapView = platform.MapLibre.MGLMapView().apply {
                // Set delegate
                setDelegate(mapDelegate)

                // Set style URL with MapTiler API key
                setStyleURL(platform.Foundation.NSURL.URLWithString(MapConfig.styleUrl))

                // Set initial camera position
                setCenterCoordinate(CLLocationCoordinate2DMake(latitude, longitude))
                setZoomLevel(zoomLevel.toDouble())

                // Enable user location if needed
                setShowsUserLocation(false)
            }

            // Notify that map is loaded
            onMapLoaded()

            mapView
        },
        update = { mapView ->
            // Update map center when coordinates change
            if (mapView.centerCoordinate.useContents { latitude } != latitude ||
                mapView.centerCoordinate.useContents { longitude } != longitude) {
                mapView.setCenterCoordinate(CLLocationCoordinate2DMake(latitude, longitude))
            }

            // Update zoom level if changed
            if (mapView.zoomLevel != zoomLevel.toDouble()) {
                mapView.setZoomLevel(zoomLevel.toDouble())
            }

            // Add GeoJSON source if available and map is loaded
            if (geoJsonNSData != null && mapView.style != null) {
                // Check if source already exists
                val existingSource = mapView.style?.sourceWithIdentifier("geojson-source")
                if (existingSource == null) {
                    // Create source
                    val source = platform.MapLibre.MGLShapeSource.alloc().initWithIdentifier(
                        "geojson-source",
                        geoJsonNSData,
                        null
                    )
                    mapView.style?.addSource(source)

                    // Add fill layer
                    val fillLayer = platform.MapLibre.MGLFillStyleLayer.alloc().initWithIdentifier(
                        "fill-layer",
                        "geojson-source"
                    )
                    fillLayer.fillColor = UIColor.colorWithRed(0.23, 0.7, 0.82, 0.5) // #3bb2d0 with 50% opacity
                    mapView.style?.addLayer(fillLayer)

                    // Add line layer
                    val lineLayer = platform.MapLibre.MGLLineStyleLayer.alloc().initWithIdentifier(
                        "line-layer",
                        "geojson-source"
                    )
                    lineLayer.lineColor = UIColor.colorWithRed(0.23, 0.7, 0.82, 1.0) // #3bb2d0
                    lineLayer.lineWidth = platform.Foundation.NSNumber.numberWithFloat(2.0)
                    mapView.style?.addLayer(lineLayer)
                } else {
                    // Update existing source
                    val source = existingSource as platform.MapLibre.MGLShapeSource
                    source.setURL(null) // Clear existing data
                    source.setGeoJSONData(geoJsonNSData)
                }
            }
        }
    )

    // Handle cleanup if needed
    DisposableEffect(Unit) {
        onDispose {
            // Any cleanup code if needed
        }
    }
}

/**
 * Delegate for MGLMapView to handle map events.
 */
@OptIn(ExperimentalForeignApi::class)
private class MapViewDelegate(
    private val onZoomChanged: (Float) -> Unit
) : NSObject(), platform.MapLibre.MGLMapViewDelegate {

    // Called when the map view's region changes
    override fun mapView(mapView: platform.MapLibre.MGLMapView, regionDidChangeWithAnimated: Boolean) {
        onZoomChanged(mapView.zoomLevel.toFloat())
    }

    // Called when the map has finished loading
    override fun mapViewDidFinishLoadingMap(mapView: platform.MapLibre.MGLMapView) {
        // Map is fully loaded
    }
}
