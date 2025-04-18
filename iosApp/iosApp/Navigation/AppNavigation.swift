import SwiftUI
import Shared

enum Screen {
    case main
    case playerName(gameCode: String)
    case createGame
    case gameLobby(gameCode: String, playerName: String)
}

struct AppNavigation: View {
    @State private var currentScreen: Screen = .main
    @State private var isLoading: Bool = false
    @State private var errorMessage: String? = nil
    
    var body: some View {
        ZStack {
            switch currentScreen {
            case .main:
                MainScreen(
                    onJoinGame: { gameCode in
                        // In a real app, we would check if the game exists
                        currentScreen = .playerName(gameCode: gameCode)
                    },
                    onCreateGame: {
                        currentScreen = .createGame
                    }
                )
                
            case .playerName(let gameCode):
                PlayerNameScreen(
                    gameCode: gameCode,
                    onJoinWithName: { code, name in
                        // In a real app, we would join the game
                        currentScreen = .gameLobby(gameCode: code, playerName: name)
                    },
                    onBack: {
                        currentScreen = .main
                    }
                )
                
            case .createGame:
                CreateGameScreen(
                    onBack: {
                        currentScreen = .main
                    }
                )
                
            case .gameLobby(let gameCode, let playerName):
                GameLobbyScreen(
                    gameCode: gameCode,
                    playerName: playerName,
                    onBack: {
                        currentScreen = .main
                    }
                )
            }
            
            // Error alert
            if let errorMessage = errorMessage {
                Color.black.opacity(0.3)
                    .edgesIgnoringSafeArea(.all)
                    .onTapGesture {
                        self.errorMessage = nil
                    }
                
                VStack {
                    Text("Error")
                        .font(.headline)
                        .padding(.bottom, 8)
                    
                    Text(errorMessage)
                        .multilineTextAlignment(.center)
                    
                    Button("OK") {
                        self.errorMessage = nil
                    }
                    .padding(.top, 16)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(12)
                .shadow(radius: 10)
                .padding(40)
            }
        }
    }
}

#Preview {
    AppNavigation()
}
