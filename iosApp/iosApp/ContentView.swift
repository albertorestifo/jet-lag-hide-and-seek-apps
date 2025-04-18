import SwiftUI
import Shared
import Combine

struct ContentView: View {
    @State private var gameCode: String = ""
    @State private var isError: Bool = false
    @State private var errorMessage: String = "Game code must be 6 characters"
    @State private var isLoading: Bool = false
    @State private var showPlayerNameScreen: Bool = false
    @State private var showCreateGameScreen: Bool = false
    @State private var errorToast: String? = nil

    // Access the shared Kotlin code directly
    private let gameService = GameService.Companion().getInstance()

    var body: some View {
        NavigationView {
            ZStack {
                VStack(spacing: 20) {
                    // Logo
                    Text("JetLag")
                        .font(.system(size: 24, weight: .medium))

                    Text("Hide & Seek")
                        .font(.system(size: 36, weight: .bold))
                        .padding(.bottom, 48)

                    // Game code input
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Game Code")
                            .font(.caption)
                            .foregroundColor(isError ? .red : .gray)

                        TextField("Enter 6-character code", text: $gameCode)
                            .font(.system(size: 16))
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(isError ? Color.red : Color.gray.opacity(0.3), lineWidth: 1)
                            )
                            .onChange(of: gameCode) { newValue in
                                // Filter and uppercase the input
                                let filtered = newValue.uppercased().filter { $0.isLetter || $0.isNumber }
                                if filtered != newValue {
                                    gameCode = filtered
                                }
                                // Limit to 6 characters
                                if gameCode.count > 6 {
                                    gameCode = String(gameCode.prefix(6))
                                }
                                isError = false
                            }
                            .autocapitalization(.allCharacters)
                            .disableAutocorrection(true)
                    }
                    .padding(.bottom, 16)

                    // Join game button
                    Button(action: {
                        if gameCode.count == 6 {
                            // Check if game exists
                            isLoading = true
                            Task {
                                do {
                                    let exists = try await gameService.checkGameExists(gameCode: gameCode)
                                    DispatchQueue.main.async {
                                        isLoading = false
                                        if exists {
                                            showPlayerNameScreen = true
                                        } else {
                                            isError = true
                                            errorMessage = "Game with code \(gameCode) does not exist"
                                        }
                                    }
                                } catch {
                                    DispatchQueue.main.async {
                                        isLoading = false
                                        isError = true
                                        errorMessage = "Error checking game: \(error.localizedDescription)"
                                    }
                                }
                            }
                        } else {
                            isError = true
                            errorMessage = "Game code must be 6 characters"
                        }
                    }) {
                        Text("Join Game")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color.blue)
                            .cornerRadius(8)
                    }

                    // Create game button
                    Button(action: {
                        showCreateGameScreen = true
                    }) {
                        Text("Create New Game")
                            .font(.headline)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }

                    if isError {
                        Text(errorMessage)
                            .foregroundColor(.red)
                            .font(.caption)
                    }

                    Spacer()
                }
                .padding()

                NavigationLink("", destination: PlayerNameView(gameCode: gameCode, gameService: gameService), isActive: $showPlayerNameScreen)
                NavigationLink("", destination: CreateGameView(), isActive: $showCreateGameScreen)

                // Loading indicator
                if isLoading {
                    Color.black.opacity(0.3)
                        .edgesIgnoringSafeArea(.all)

                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(1.5)
                        .foregroundColor(.white)
                }

                // Error toast
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
            .navigationBarHidden(true)
        }
    }
}

struct PlayerNameView: View {
    let gameCode: String
    let gameService: GameService
    var onDismiss: (() -> Void)? = nil

    @State private var playerName: String = ""
    @State private var isError: Bool = false
    @State private var isLoading: Bool = false
    @State private var errorToast: String? = nil
    @State private var showGameLobby: Bool = false
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        ZStack {
            VStack(spacing: 20) {
                // Top bar with back button
                HStack {
                    Button(action: {
                        onDismiss?()
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .font(.system(size: 20))
                            .foregroundColor(.blue)
                    }

                    Text("Join Game: \(gameCode)")
                        .font(.headline)
                        .padding(.leading, 8)

                    Spacer()
                }
                .padding(.bottom, 24)

                // Player name input
                VStack(alignment: .leading, spacing: 8) {
                    Text("Your Name")
                        .font(.caption)
                        .foregroundColor(isError ? .red : .gray)

                    TextField("Enter your name", text: $playerName)
                        .font(.system(size: 16))
                        .padding()
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(isError ? Color.red : Color.gray.opacity(0.3), lineWidth: 1)
                        )
                        .onChange(of: playerName) { _ in
                            isError = false
                        }
                        .autocapitalization(.words)
                }
                .padding(.bottom, 16)

                // Join game button
                Button(action: {
                    if !playerName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        // Join the game
                        isLoading = true
                        Task {
                            do {
                                let success = try await gameService.joinGame(gameCode: gameCode, playerName: playerName)
                                DispatchQueue.main.async {
                                    isLoading = false
                                    if success {
                                        // Successfully joined the game and connected to WebSocket
                                        showGameLobby = true
                                    }
                                }
                            } catch {
                                DispatchQueue.main.async {
                                    isLoading = false
                                    errorToast = "Error joining game: \(error.localizedDescription)"
                                }
                            }
                        }
                    } else {
                        isError = true
                    }
                }) {
                    Text("Join Game")
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(8)
                }

                if isError {
                    Text("Please enter your name")
                        .foregroundColor(.red)
                        .font(.caption)
                }

                Spacer()

                NavigationLink("", destination: GameLobbyView(gameCode: gameCode, playerName: playerName, gameService: gameService), isActive: $showGameLobby)

                // Loading indicator
                if isLoading {
                    Color.black.opacity(0.3)
                        .edgesIgnoringSafeArea(.all)

                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(1.5)
                        .foregroundColor(.white)
                }

                // Error toast
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
            .padding()
        }
        .navigationBarHidden(true)
    }
}

struct CreateGameView: View {
    var onDismiss: (() -> Void)? = nil
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        VStack(spacing: 20) {
            // Top bar with back button
            HStack {
                Button(action: {
                    onDismiss?()
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 20))
                        .foregroundColor(.blue)
                }

                Text("Create New Game")
                    .font(.headline)
                    .padding(.leading, 8)

                Spacer()
            }
            .padding(.bottom, 24)

            // Placeholder content
            Spacer()

            VStack(spacing: 16) {
                Text("Game Creation Wizard")
                    .font(.title)
                    .multilineTextAlignment(.center)

                Text("This screen will be implemented in a future update.")
                    .multilineTextAlignment(.center)
                    .padding()

                Button(action: {
                    onDismiss?()
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Go Back")
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

struct GameLobbyView: View {
    let gameCode: String
    let playerName: String
    let gameService: GameService
    var onDismiss: (() -> Void)? = nil
    @Environment(\.presentationMode) var presentationMode

    // State for the view
    @State private var players: [Player] = []
    @State private var isConnected: Bool = false
    @State private var errorToast: String? = nil

    // Timer to periodically update the player list
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    // Get the game state
    private var gameState: GameState {
        return GameState.Companion().getInstance()
    }

    var body: some View {
        VStack(spacing: 20) {
            // Top bar with back button
            HStack {
                Button(action: {
                    onDismiss?()
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 20))
                        .foregroundColor(.blue)
                }

                Text("Game: \(gameCode)")
                    .font(.headline)
                    .padding(.leading, 8)

                Spacer()

                // Connection status indicator
                if gameState.isConnectedFlow.value {
                    Text("Connected")
                        .foregroundColor(.green)
                        .font(.caption)
                } else {
                    Text("Disconnected")
                        .foregroundColor(.red)
                        .font(.caption)
                }
            }
            .padding(.bottom, 24)

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
                    Text(gameState.gameFlow.value?.status ?? "Waiting")
                }

                HStack {
                    Text("Players:")
                        .fontWeight(.medium)
                    Text("\(gameState.playersFlow.value.count)")
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

            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(gameState.playersFlow.value, id: \.id) { player in
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
                            Text(player.name + (player.id == gameState.currentPlayerIdFlow.value ? " (You)" : ""))
                                .fontWeight(player.id == gameState.currentPlayerIdFlow.value ? .bold : .regular)
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
                                .fill(player.id == gameState.currentPlayerIdFlow.value ? Color.blue.opacity(0.1) : Color.white)
                                .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
                        )
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
            }

            Spacer()
        }
        .padding()
        .navigationBarHidden(true)
        .onReceive(timer) { _ in
            // Force UI update by accessing the state
            let _ = gameState.playersFlow.value
            let _ = gameState.isConnectedFlow.value
            let _ = gameState.currentPlayerIdFlow.value
            let _ = gameState.gameFlow.value

            // Check for errors
            if let error = gameState.errorFlow.value {
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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
