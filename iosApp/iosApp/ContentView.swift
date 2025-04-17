import SwiftUI
import Shared

struct ContentView: View {
    @State private var showContent = false

    // Get environment information from BuildConfig
    let isDevelopment: Bool = BuildConfigKt.isDevelopment
    let apiBaseUrl: String = BuildConfigKt.getApiBaseUrl()
    let webSocketUrl: String = BuildConfigKt.getWebSocketUrl()

    var body: some View {
        VStack(spacing: 20) {
            // Environment information card
            VStack(alignment: .leading, spacing: 8) {
                Text("Environment: \(isDevelopment ? "DEVELOPMENT" : "PRODUCTION")")
                    .font(.headline)
                Text("API URL: \(apiBaseUrl)")
                    .font(.subheadline)
                Text("WebSocket URL: \(webSocketUrl)")
                    .font(.subheadline)
            }
            .padding()
            .background(Color(.secondarySystemBackground))
            .cornerRadius(10)
            .padding(.horizontal)

            Button("Click me!") {
                withAnimation {
                    showContent = !showContent
                }
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet())")
                }
                .transition(.move(edge: .top).combined(with: .opacity))
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
