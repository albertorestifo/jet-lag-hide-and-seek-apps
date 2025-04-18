import SwiftUI
import Shared

struct GameLobbyScreen: View {
    let gameCode: String
    let playerName: String
    var onBack: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            // Top bar with back button
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 20))
                        .foregroundColor(.blue)
                }
                
                Text("Game: \(gameCode)")
                    .font(.headline)
                    .padding(.leading, 8)
                
                Spacer()
            }
            .padding(.bottom, 24)
            
            // Placeholder content
            Spacer()
            
            VStack(spacing: 16) {
                Text("Welcome, \(playerName)!")
                    .font(.title)
                    .multilineTextAlignment(.center)
                
                Text("You've joined game \(gameCode).")
                    .multilineTextAlignment(.center)
                
                Text("The game lobby and gameplay screens will be implemented in a future update.")
                    .multilineTextAlignment(.center)
                    .padding()
                
                Button(action: onBack) {
                    Text("Leave Game")
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(width: 200)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(8)
                }
                .padding(.top, 32)
            }
            
            Spacer()
        }
        .padding()
        .navigationBarHidden(true)
    }
}

#Preview {
    GameLobbyScreen(
        gameCode: "ABC123",
        playerName: "John Doe",
        onBack: { }
    )
}
