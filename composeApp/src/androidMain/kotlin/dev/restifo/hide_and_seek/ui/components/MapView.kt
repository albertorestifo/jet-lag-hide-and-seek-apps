package dev.restifo.hide_and_seek.ui.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import dev.restifo.hide_and_seek.config.MapConfig
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.sources.GeoJsonSource

/**
 * A MapTiler/MapLibre component for Android.
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
    val context = LocalContext.current
    var mapView: MapView? = null
    var mapLoaded by remember { mutableStateOf(false) }

    // Initialize MapLibre if not already initialized
    LaunchedEffect(Unit) {
        if (!MapLibre.isInitialized()) {
            MapLibre.getInstance(context)
        }
    }

    // Create and configure the MapView
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                mapView = this
                onCreate(Bundle())
                getMapAsync { map ->
                    // Set the map style using MapTiler
                    map.setStyle(MapConfig.styleUrl) { style ->
                        // Map is ready
                        mapLoaded = true
                        onMapLoaded()

                        // Set initial camera position
                        val position = CameraPosition.Builder()
                            .target(LatLng(latitude, longitude))
                            .zoom(zoomLevel.toDouble())
                            .build()
                        map.cameraPosition = position

                        // Add GeoJSON source if available
                        if (!geoJsonData.isNullOrEmpty()) {
                            try {
                                val source = GeoJsonSource("geojson-source", geoJsonData)
                                style.addSource(source)

                                // Add fill layer
                                val fillLayer = FillLayer("fill-layer", "geojson-source")
                                fillLayer.setProperties(
                                    org.maplibre.android.style.layers.PropertyFactory.fillOpacity(0.5f),
                                    org.maplibre.android.style.layers.PropertyFactory.fillColor("#3bb2d0")
                                )
                                style.addLayer(fillLayer)

                                // Add line layer
                                val lineLayer = LineLayer("line-layer", "geojson-source")
                                lineLayer.setProperties(
                                    org.maplibre.android.style.layers.PropertyFactory.lineColor("#3bb2d0"),
                                    org.maplibre.android.style.layers.PropertyFactory.lineWidth(2f)
                                )
                                style.addLayer(lineLayer)
                            } catch (e: Exception) {
                                // Handle GeoJSON parsing errors
                                e.printStackTrace()
                            }
                        }
                    }

                    // Listen for camera movement to update zoom level
                    map.addOnCameraIdleListener {
                        if (mapLoaded && map.cameraPosition.zoom.toFloat() != zoomLevel) {
                            onZoomChanged(map.cameraPosition.zoom.toFloat())
                        }
                    }

                    // Update camera position when coordinates change
                    if (map.cameraPosition.target.latitude != latitude ||
                        map.cameraPosition.target.longitude != longitude) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLng(LatLng(latitude, longitude))
                        )
                    }
                }
            }
        },
        update = { view ->
            mapView = view
            view.getMapAsync { map ->
                if (mapLoaded) {
                    // Update camera position when coordinates change
                    if (map.cameraPosition.target.latitude != latitude ||
                        map.cameraPosition.target.longitude != longitude) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLng(LatLng(latitude, longitude))
                        )
                    }

                    // Update zoom level if changed
                    if (map.cameraPosition.zoom.toFloat() != zoomLevel) {
                        map.animateCamera(
                            CameraUpdateFactory.zoomTo(zoomLevel.toDouble())
                        )
                    }

                    // Update GeoJSON if changed
                    if (!geoJsonData.isNullOrEmpty()) {
                        map.getStyle { style ->
                            val source = style.getSource("geojson-source") as? GeoJsonSource
                            if (source != null) {
                                source.setGeoJson(geoJsonData)
                            } else {
                                // Add source and layers if they don't exist
                                try {
                                    val newSource = GeoJsonSource("geojson-source", geoJsonData)
                                    style.addSource(newSource)

                                    // Add fill layer
                                    val fillLayer = FillLayer("fill-layer", "geojson-source")
                                    fillLayer.setProperties(
                                        org.maplibre.android.style.layers.PropertyFactory.fillOpacity(0.5f),
                                        org.maplibre.android.style.layers.PropertyFactory.fillColor("#3bb2d0")
                                    )
                                    style.addLayer(fillLayer)

                                    // Add line layer
                                    val lineLayer = LineLayer("line-layer", "geojson-source")
                                    lineLayer.setProperties(
                                        org.maplibre.android.style.layers.PropertyFactory.lineColor("#3bb2d0"),
                                        org.maplibre.android.style.layers.PropertyFactory.lineWidth(2f)
                                    )
                                    style.addLayer(lineLayer)
                                } catch (e: Exception) {
                                    // Handle GeoJSON parsing errors
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    // Handle MapView lifecycle
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}
