import SwiftUI
import Shared
import Mapbox

@main
struct iOSApp: App {
    init() {
        // Initialize the app with the correct environment
        HideAndSeekApp().initialize(isDevelopment: false)

        // Get MapTiler API key from environment variable
        let apiKey = ProcessInfo.processInfo.environment["MAPTILER_API_KEY"] ?? ""

        if apiKey.isEmpty {
            // Log error or show a message to the user
            print("WARNING: MapTiler API key not found. Maps will not work correctly.")
            print("Please set the MAPTILER_API_KEY environment variable.")
        }

        // Set MapTiler API key
        MapTilerConfig.initialize(apiKey: apiKey)

        // Set access token for Mapbox SDK
        MGLAccountManager.accessToken = apiKey

        // Restore connection if available
        Task {
            do {
                let restored = try await GameService.Companion().getInstance().restoreConnection()
                print("Connection restored: \((restored as? Bool) == true)")
            } catch {
                print("Error restoring connection: \(error)")
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}