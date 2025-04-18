import SwiftUI
import Shared

struct PlayerListView: View {
    let players: [Player]
    let currentPlayerId: String?
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 8) {
                ForEach(players, id: \.id) { player in
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
                        Text(player.name + (player.id == currentPlayerId ? " (You)" : ""))
                            .fontWeight(player.id == currentPlayerId ? .bold : .regular)
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
                            .fill(player.id == currentPlayerId ? Color.blue.opacity(0.1) : Color.white)
                            .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
                    )
                }
            }
        }
    }
}
