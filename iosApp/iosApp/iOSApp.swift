import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Initialize the app with the correct environment
        #if DEBUG
        IosAppInitializerKt.doInitialize(isDevelopment: true)
        #else
        IosAppInitializerKt.doInitialize(isDevelopment: false)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}