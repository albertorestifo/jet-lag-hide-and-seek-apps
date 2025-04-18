import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class GameLobbyScreenTests: XCTestCase {
    
    func testGameLobbyScreenInitialState() throws {
        // Given
        let gameCode = "ABC123"
        let playerName = "John Doe"
        let gameLobbyView = GameLobbyView(gameCode: gameCode, playerName: playerName)
        
        // When
        let header = try gameLobbyView.inspect().vStack().hStack(0).text(1).string()
        let welcomeText = try gameLobbyView.inspect().vStack().vStack(1).text(0).string()
        let gameCodeText = try gameLobbyView.inspect().vStack().vStack(1).text(1).string()
        let placeholderText = try gameLobbyView.inspect().vStack().vStack(1).text(2).string()
        let button = try gameLobbyView.inspect().vStack().vStack(1).button(3)
        
        // Then
        XCTAssertEqual(header, "Game: \(gameCode)")
        XCTAssertEqual(welcomeText, "Welcome, \(playerName)!")
        XCTAssertEqual(gameCodeText, "You've joined game \(gameCode).")
        XCTAssertEqual(placeholderText, "The game lobby and gameplay screens will be implemented in a future update.")
        XCTAssertEqual(try button.labelView().text().string(), "Leave Game")
    }
    
    func testBackButtonNavigation() throws {
        // Given
        var backCalled = false
        let gameLobbyView = GameLobbyView(
            gameCode: "ABC123",
            playerName: "John Doe",
            onDismiss: { backCalled = true }
        )
        
        // When
        try gameLobbyView.inspect().vStack().hStack(0).button(0).tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
    
    func testLeaveGameButtonNavigation() throws {
        // Given
        var backCalled = false
        let gameLobbyView = GameLobbyView(
            gameCode: "ABC123",
            playerName: "John Doe",
            onDismiss: { backCalled = true }
        )
        
        // When
        try gameLobbyView.inspect().vStack().vStack(1).button(3).tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
}
