import SwiftUI
import Shared

struct PlayerNameScreen: View {
    let gameCode: String
    var onJoinWithName: (String, String) -> Void
    var onBack: () -> Void
    
    @State private var playerName: String = ""
    @State private var isError: Bool = false
    @State private var isLoading: Bool = false
    
    var body: some View {
        ZStack {
            VStack(spacing: 20) {
                // Top bar with back button
                HStack {
                    Button(action: onBack) {
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
                        onJoinWithName(gameCode, playerName)
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
                .disabled(isLoading)
                
                if isError {
                    Text("Please enter your name")
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
    PlayerNameScreen(
        gameCode: "ABC123",
        onJoinWithName: { _, _ in },
        onBack: { }
    )
}
