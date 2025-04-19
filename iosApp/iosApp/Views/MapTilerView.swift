import SwiftUI
import Mapbox

struct MapTilerView: UIViewRepresentable {
    var latitude: Double
    var longitude: Double
    var zoomLevel: Float
    var geoJsonData: String?
    var onMapLoaded: () -> Void
    var onZoomChanged: ((Float) -> Void)?
    
    func makeUIView(context: Context) -> MGLMapView {
        // Create the map view
        let mapView = MGLMapView()
        mapView.delegate = context.coordinator
        
        // Set the style URL with the API key
        if let styleURL = URL(string: "https://api.maptiler.com/maps/streets/style.json?key=\(MapTilerConfig.apiKey)") {
            mapView.styleURL = styleURL
        }
        
        // Set initial camera position
        mapView.setCenter(CLLocationCoordinate2D(latitude: latitude, longitude: longitude), animated: false)
        mapView.setZoomLevel(Double(zoomLevel), animated: false)
        
        // Disable user location for now
        mapView.showsUserLocation = false
        
        // Notify that map is loaded
        DispatchQueue.main.async {
            onMapLoaded()
        }
        
        return mapView
    }
    
    func updateUIView(_ mapView: MGLMapView, context: Context) {
        // Update camera position if needed
        if mapView.centerCoordinate.latitude != latitude || mapView.centerCoordinate.longitude != longitude {
            mapView.setCenter(CLLocationCoordinate2D(latitude: latitude, longitude: longitude), animated: true)
        }
        
        // Update zoom level if needed
        if mapView.zoomLevel != Double(zoomLevel) {
            mapView.setZoomLevel(Double(zoomLevel), animated: true)
        }
        
        // Add GeoJSON if available and style is loaded
        if let geoJsonData = geoJsonData, let data = geoJsonData.data(using: .utf8), mapView.style != nil {
            // Check if source already exists
            if let existingSource = mapView.style?.source(withIdentifier: "geojson-source") as? MGLShapeSource {
                // Update existing source
                if let shape = try? MGLShape(data: data, encoding: String.Encoding.utf8.rawValue) {
                    existingSource.shape = shape
                }
            } else {
                // Create new source and layers
                if let shape = try? MGLShape(data: data, encoding: String.Encoding.utf8.rawValue) {
                    let source = MGLShapeSource(identifier: "geojson-source", shape: shape, options: nil)
                    mapView.style?.addSource(source)
                    
                    // Add fill layer
                    let fillLayer = MGLFillStyleLayer(identifier: "fill-layer", source: source)
                    fillLayer.fillColor = NSExpression(forConstantValue: UIColor(red: 0.23, green: 0.7, blue: 0.82, alpha: 0.5))
                    mapView.style?.addLayer(fillLayer)
                    
                    // Add line layer
                    let lineLayer = MGLLineStyleLayer(identifier: "line-layer", source: source)
                    lineLayer.lineColor = NSExpression(forConstantValue: UIColor(red: 0.23, green: 0.7, blue: 0.82, alpha: 1.0))
                    lineLayer.lineWidth = NSExpression(forConstantValue: 2.0)
                    mapView.style?.addLayer(lineLayer)
                }
            }
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, MGLMapViewDelegate {
        var parent: MapTilerView
        
        init(_ parent: MapTilerView) {
            self.parent = parent
        }
        
        func mapView(_ mapView: MGLMapView, regionDidChangeWith reason: MGLCameraChangeReason, animated: Bool) {
            // Notify parent of zoom change
            parent.onZoomChanged?(Float(mapView.zoomLevel))
        }
        
        func mapViewDidFinishLoadingMap(_ mapView: MGLMapView) {
            // Map is fully loaded
        }
    }
}
