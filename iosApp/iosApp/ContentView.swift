import SwiftUI
import Shared

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
    private let gameState = GameState.Companion().getInstance()

    var body: some View {
        NavigationView {
            ZStack {
                VStack(spacing: 24) {
                    Spacer()

                    // Logo or title
                    Text("Hide and Seek")
                        .font(.largeTitle)
                        .fontWeight(.bold)

                    Spacer()

                    // Join game section
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Join a Game")
                            .font(.headline)

                        TextField("Enter Game Code", text: $gameCode)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .autocapitalization(.allCharacters)
                            .onChange(of: gameCode) { _ in
                                isError = false
                            }

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
                                            if (exists as? Bool) == true {
                                                showPlayerNameScreen = true
                                            } else {
                                                isError = true
                                                errorMessage = "Game with code \\(gameCode) does not exist"
                                            }
                                        }
                                    } catch {
                                        DispatchQueue.main.async {
                                            isLoading = false
                                            isError = true
                                            errorMessage = "Error checking game: \\(error.localizedDescription)"
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

                        if isError {
                            Text(errorMessage)
                                .foregroundColor(.red)
                                .font(.caption)
                        }

                        Spacer()
                    }
                    .padding()

                    NavigationLink(destination: PlayerNameView(gameCode: gameCode, gameService: gameService), isActive: $showPlayerNameScreen) {
                        EmptyView()
                    }

                    NavigationLink(destination: Text("Create Game Screen"), isActive: $showCreateGameScreen) {
                        EmptyView()
                    }

                    // Create game button
                    Button(action: {
                        showCreateGameScreen = true
                    }) {
                        Text("Create a New Game")
                            .font(.headline)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.blue, lineWidth: 2)
                            )
                    }
                    .padding(.horizontal)

                    Spacer()
                }

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
            VStack(spacing: 24) {
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

                    Spacer()

                    Text("Join Game: \(gameCode)")
                        .font(.headline)

                    Spacer()
                }
                .padding(.bottom, 24)

                Spacer()

                // Player name input
                VStack(alignment: .leading, spacing: 16) {
                    Text("Your Name")
                        .font(.headline)

                    TextField("Enter your name", text: $playerName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .onChange(of: playerName) { _ in
                            isError = false
                        }

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
                                        if (success as? Bool) == true {
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
                }
                .padding()

                Spacer()

                NavigationLink(destination: Text("Game Lobby"), isActive: $showGameLobby) {
                    EmptyView()
                }
            }
            .padding()

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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
