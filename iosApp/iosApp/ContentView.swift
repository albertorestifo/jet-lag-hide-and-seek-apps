import SwiftUI
import Shared

struct ContentView: View {
    @State private var gameCode: String = ""
    @State private var isError: Bool = false
    @State private var showPlayerNameScreen: Bool = false
    @State private var showCreateGameScreen: Bool = false

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
                            showPlayerNameScreen = true
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
                        Text("Game code must be 6 characters")
                            .foregroundColor(.red)
                            .font(.caption)
                    }

                    Spacer()
                }
                .padding()

                NavigationLink("", destination: PlayerNameView(gameCode: gameCode), isActive: $showPlayerNameScreen)
                NavigationLink("", destination: CreateGameView(), isActive: $showCreateGameScreen)
            }
            .navigationBarHidden(true)
        }
    }
}

struct PlayerNameView: View {
    let gameCode: String
    var onDismiss: (() -> Void)? = nil
    @State private var playerName: String = ""
    @State private var isError: Bool = false
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
                        showGameLobby = true
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

                NavigationLink("", destination: GameLobbyView(gameCode: gameCode, playerName: playerName), isActive: $showGameLobby)
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

                Button(action: {
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
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
