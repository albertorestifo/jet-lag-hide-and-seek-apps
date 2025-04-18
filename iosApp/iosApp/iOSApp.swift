import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Initialize the app with the correct environment
        #if DEBUG
        IosAppInitializer.shared.doInitialize(isDevelopment: true)
        #else
        IosAppInitializer.shared.doInitialize(isDevelopment: false)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}