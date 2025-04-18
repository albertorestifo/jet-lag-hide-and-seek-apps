import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Initialize the app with the correct environment
        HideAndSeekApp().initialize(isDevelopment: false)

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