import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Initialize the app with the correct environment
        HideAndSeekApp().initialize(isDevelopment: false)

        // Restore connection if available
        Task {
            let _ = try? await GameService.Companion().getInstance().restoreConnection()
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}