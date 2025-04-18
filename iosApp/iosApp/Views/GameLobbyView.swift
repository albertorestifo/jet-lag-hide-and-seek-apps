import SwiftUI
import Shared

struct GameLobbyView: View {
    let gameCode: String
    let playerName: String
    var onDismiss: (() -> Void)? = nil
    @Environment(\.presentationMode) var presentationMode
    
    // Access the shared Kotlin code directly
    private let gameService = GameService.Companion().getInstance()
    private let gameState = GameState.Companion().getInstance()
    
    // State for the view
    @State private var players: [Player] = []
    @State private var currentPlayerId: String? = nil
    @State private var isConnected: Bool = false
    @State private var gameStatus: String = "Waiting"
    @State private var errorToast: String? = nil
    
    // Timer to periodically update the player list
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        VStack(spacing: 20) {
            // Navigation bar
            HStack {
                Text("Game: \(gameCode)")
                    .font(.headline)
                    .padding(.leading, 8)
                
                Spacer()
                
                // Connection status indicator
                if isConnected {
                    Text("Connected")
                        .foregroundColor(.green)
                        .font(.caption)
                } else {
                    Text("Disconnected")
                        .foregroundColor(.red)
                        .font(.caption)
                }
            }
            .padding(.horizontal)
            
            // Game info card
            VStack(alignment: .leading, spacing: 8) {
                Text("Welcome, \(playerName)!")
                    .font(.title)
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: .infinity)
                    .padding(.bottom, 8)
                
                HStack {
                    Text("Status:")
                        .fontWeight(.medium)
                    Text(gameStatus)
                }
                
                HStack {
                    Text("Players:")
                        .fontWeight(.medium)
                    Text("\(players.count)")
                }
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.gray.opacity(0.1))
            )
            .padding(.bottom, 16)
            
            // Players list
            HStack {
                Text("Players")
                    .font(.headline)
                Spacer()
            }
            .padding(.vertical, 8)
            .padding(.horizontal)
            
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(players, id: \\.id) { player in
                        PlayerRow(player: player, isCurrentPlayer: player.id == currentPlayerId)
                            .padding(.horizontal)
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.bottom, 16)
            
            Button(action: {
                // Leave the game
                gameService.leaveGame()
                onDismiss?()
                presentationMode.wrappedValue.dismiss()
            }) {
                Text("Leave Game")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(width: 200)
                    .frame(height: 50)
                    .background(Color.blue)
                    .cornerRadius(8)
            }
            .padding(.top, 32)
            
            Spacer()
        }
        .padding()
        .navigationBarHidden(true)
        .onReceive(timer) { _ in
            // Force UI update by accessing the state
            players = gameState.playersFlow.value as? [Player] ?? []
            isConnected = (gameState.isConnectedFlow.value as? Bool) == true
            currentPlayerId = gameState.currentPlayerIdFlow.value as? String
            
            if let game = gameState.gameFlow.value as? Game {
                gameStatus = game.status
            }
            
            // Check for errors
            if let error = gameState.errorFlow.value as? String {
                errorToast = error
                // Clear the error in the game state
                gameState.setError(error: nil)
            }
        }
        .overlay(
            // Error toast
            Group {
                if let error = errorToast {
                    VStack {
                        Spacer()
                        Text(error)
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.red.opacity(0.8))
                            .cornerRadius(8)
                            .padding(.bottom, 20)
                            .onAppear {
                                // Auto-dismiss after 3 seconds
                                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                    errorToast = nil
                                }
                            }
                    }
                }
            }
        )
    }
}

struct PlayerRow: View {
    let player: Player
    let isCurrentPlayer: Bool
    
    var body: some View {
        HStack {
            // Player icon
            if player.is_creator {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
            } else {
                Image(systemName: "person.fill")
                    .foregroundColor(.blue)
            }
            
            // Player name
            Text(player.name + (isCurrentPlayer ? " (You)" : ""))
                .fontWeight(isCurrentPlayer ? .bold : .regular)
                .padding(.leading, 8)
            
            Spacer()
            
            // Creator label
            if player.is_creator {
                Text("Creator")
                    .font(.caption)
                    .foregroundColor(.blue)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(isCurrentPlayer ? Color.blue.opacity(0.1) : Color.white)
                .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
    }
}
