import SwiftUI
import MapKit

struct MapView: UIViewRepresentable {
    var latitude: Double
    var longitude: Double
    var zoomLevel: Float
    var onMapLoaded: () -> Void
    var onZoomChanged: ((Float) -> Void)? = nil

    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator

        // Set initial region
        let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        // Convert zoom level to span (approximate conversion)
        let span = calculateSpan(fromZoomLevel: zoomLevel)
        let region = MKCoordinateRegion(center: coordinate, span: span)
        mapView.setRegion(region, animated: true)

        // Notify that map is loaded
        DispatchQueue.main.async {
            onMapLoaded()
        }

        return mapView
    }

    func updateUIView(_ mapView: MKMapView, context: Context) {
        // Update map region when coordinates change
        let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        let span = calculateSpan(fromZoomLevel: zoomLevel)
        let region = MKCoordinateRegion(center: coordinate, span: span)
        mapView.setRegion(region, animated: true)
    }

    // Helper function to convert zoom level to span
    private func calculateSpan(fromZoomLevel zoomLevel: Float) -> MKCoordinateSpan {
        // This is an approximation - zoom levels work differently in different map providers
        let delta = Double(360 / pow(2, Double(zoomLevel)))
        return MKCoordinateSpan(latitudeDelta: delta, longitudeDelta: delta)
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, MKMapViewDelegate {
        var parent: MapView

        init(_ parent: MapView) {
            self.parent = parent
        }

        func mapView(_ mapView: MKMapView, regionDidChangeAnimated animated: Bool) {
            // Calculate zoom level from region span
            let span = mapView.region.span
            let avgSpan = (span.latitudeDelta + span.longitudeDelta) / 2.0
            let zoomLevel = Float(log2(360 / avgSpan))

            // Notify parent of zoom change
            parent.onZoomChanged?(zoomLevel)
        }
    }
}
