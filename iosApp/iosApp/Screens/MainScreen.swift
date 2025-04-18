import SwiftUI
import Shared

struct MainScreen: View {
    @State private var gameCode: String = ""
    @State private var isError: Bool = false
    @State private var isLoading: Bool = false
    @State private var errorMessage: String = ""
    
    var onJoinGame: (String) -> Void
    var onCreateGame: () -> Void
    
    var body: some View {
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
                        onJoinGame(gameCode)
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
                .disabled(isLoading)
                
                // Create game button
                Button(action: {
                    onCreateGame()
                }) {
                    Text("Create New Game")
                        .font(.headline)
                        .foregroundColor(.blue)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                }
                .disabled(isLoading)
                
                if isError {
                    Text(errorMessage.isEmpty ? "Invalid game code" : errorMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Spacer()
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
        }
        .navigationBarHidden(true)
    }
}

#Preview {
    MainScreen(
        onJoinGame: { _ in },
        onCreateGame: { }
    )
}
