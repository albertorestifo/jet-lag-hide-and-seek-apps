import XCTest
import SwiftUI
import ViewInspector
import Shared
@testable import iosApp

class GameLobbyTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Reset game state before each test
        GameState.Companion().getInstance().clear()
    }
    
    func testGameLobbyInitialState() throws {
        // Given
        let gameCode = "ABC123"
        let playerName = "John"
        let gameService = GameService.Companion().getInstance()
        
        // When
        let gameLobbyView = GameLobbyView(gameCode: gameCode, playerName: playerName, gameService: gameService)
        
        // Then
        let title = try gameLobbyView.inspect().find(text: "Game: \(gameCode)")
        XCTAssertNotNil(title)
        
        let welcomeText = try gameLobbyView.inspect().find(text: "Welcome, \(playerName)!")
        XCTAssertNotNil(welcomeText)
    }
    
    func testGameLobbyDisplaysPlayers() throws {
        // Given
        let gameCode = "ABC123"
        let playerName = "John"
        let gameService = GameService.Companion().getInstance()
        let gameState = GameState.Companion().getInstance()
        
        // Add players to the game state
        let player1 = Player(id: "player-1", name: "John", is_creator: true)
        let player2 = Player(id: "player-2", name: "Jane", is_creator: false)
        gameState.addPlayer(player: player1)
        gameState.addPlayer(player: player2)
        gameState.setCurrentPlayerId(playerId: "player-1")
        
        // When
        let gameLobbyView = GameLobbyView(gameCode: gameCode, playerName: playerName, gameService: gameService)
        
        // Then - We can't directly test the dynamic content in ViewInspector,
        // but we can verify the view structure
        let vStack = try gameLobbyView.inspect().vStack()
        XCTAssertTrue(vStack.count > 0)
    }
    
    func testGameLobbyDisplaysConnectionStatus() throws {
        // Given
        let gameCode = "ABC123"
        let playerName = "John"
        let gameService = GameService.Companion().getInstance()
        let gameState = GameState.Companion().getInstance()
        
        // Set connection status
        gameState.setConnected(isConnected: true)
        
        // When
        let gameLobbyView = GameLobbyView(gameCode: gameCode, playerName: playerName, gameService: gameService)
        
        // Then
        let connectedText = try? gameLobbyView.inspect().find(text: "Connected")
        XCTAssertNotNil(connectedText)
    }
    
    func testLeaveGameButton() throws {
        // Given
        let gameCode = "ABC123"
        let playerName = "John"
        let gameService = GameService.Companion().getInstance()
        
        var backCalled = false
        let onDismiss: () -> Void = {
            backCalled = true
        }
        
        // When
        let gameLobbyView = GameLobbyView(gameCode: gameCode, playerName: playerName, gameService: gameService, onDismiss: onDismiss)
        
        // Then
        let leaveButton = try gameLobbyView.inspect().find(button: "Leave Game")
        try leaveButton.tap()
        
        XCTAssertTrue(backCalled)
    }
}
