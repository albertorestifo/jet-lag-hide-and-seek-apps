import SwiftUI
import Shared

struct CreateGameScreen: View {
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
                
                Button(action: onBack) {
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

#Preview {
    CreateGameScreen(onBack: { })
}
