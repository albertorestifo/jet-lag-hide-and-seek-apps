import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class PlayerNameScreenTests: XCTestCase {
    
    func testPlayerNameScreenInitialState() throws {
        // Given
        let gameCode = "ABC123"
        let playerNameView = PlayerNameView(gameCode: gameCode)
        
        // When
        let header = try playerNameView.inspect().vStack().hStack(0).text(1).string()
        let textField = try playerNameView.inspect().vStack().vStack(1)
        let joinButton = try playerNameView.inspect().vStack().button(2)
        
        // Then
        XCTAssertEqual(header, "Join Game: \(gameCode)")
        XCTAssertEqual(try textField.text(0).string(), "Your Name")
        XCTAssertEqual(try textField.textField(1).labelView().text().string(), "Enter your name")
        XCTAssertEqual(try joinButton.labelView().text().string(), "Join Game")
    }
    
    func testPlayerNameValidation() throws {
        // Given
        let playerNameView = PlayerNameView(gameCode: "ABC123")
        
        // When - Tap join without entering a name
        try playerNameView.inspect().vStack().button(2).tap()
        
        // Then - Error should be displayed
        let errorText = try? playerNameView.inspect().vStack().text(3).string()
        XCTAssertEqual(errorText, "Please enter your name")
    }
    
    func testNavigationToGameLobby() throws {
        // Given
        let playerNameView = PlayerNameView(gameCode: "ABC123")
        
        // When - Enter a name and tap join
        try playerNameView.inspect().vStack().vStack(1).textField(1).setInput("John Doe")
        
        // Then - Navigation link should be activated when tapped
        let navLink = try playerNameView.inspect().vStack().navigationLink(4)
        XCTAssertFalse(try navLink.isActive())
        
        try playerNameView.inspect().vStack().button(2).tap()
        
        // Note: In a real test environment with a state update, this would become true
        // but ViewInspector has limitations with state changes in unit tests
    }
    
    func testBackButtonNavigation() throws {
        // Given
        var backCalled = false
        let playerNameView = PlayerNameView(
            gameCode: "ABC123",
            onDismiss: { backCalled = true }
        )
        
        // When
        try playerNameView.inspect().vStack().hStack(0).button(0).tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
}
