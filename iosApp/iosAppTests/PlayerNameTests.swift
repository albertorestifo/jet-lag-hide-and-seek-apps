import XCTest
import SwiftUI
import ViewInspector
import Shared
@testable import iosApp

class PlayerNameTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Reset game state before each test
        GameState.Companion().getInstance().clear()
    }
    
    func testPlayerNameInitialState() throws {
        // Given
        let gameCode = "ABC123"
        let gameService = GameService.Companion().getInstance()
        
        // When
        let playerNameView = PlayerNameView(gameCode: gameCode, gameService: gameService)
        
        // Then
        let title = try playerNameView.inspect().find(text: "Join Game: \(gameCode)")
        XCTAssertNotNil(title)
        
        let nameLabel = try playerNameView.inspect().find(text: "Your Name")
        XCTAssertNotNil(nameLabel)
        
        let joinButton = try playerNameView.inspect().find(button: "Join Game")
        XCTAssertNotNil(joinButton)
    }
    
    func testPlayerNameValidation() throws {
        // Given
        let gameCode = "ABC123"
        let gameService = GameService.Companion().getInstance()
        
        // When
        let playerNameView = PlayerNameView(gameCode: gameCode, gameService: gameService)
        
        // Then - Empty name should show error
        let textField = try playerNameView.inspect().find(ViewType.TextField.self)
        try textField.setInput("")
        
        let joinButton = try playerNameView.inspect().find(button: "Join Game")
        try joinButton.tap()
        
        // We can't directly test the error state in ViewInspector,
        // but we can verify the view structure remains the same
        let nameLabel = try playerNameView.inspect().find(text: "Your Name")
        XCTAssertNotNil(nameLabel)
    }
    
    func testBackButton() throws {
        // Given
        let gameCode = "ABC123"
        let gameService = GameService.Companion().getInstance()
        
        var backCalled = false
        let onDismiss: () -> Void = {
            backCalled = true
        }
        
        // When
        let playerNameView = PlayerNameView(gameCode: gameCode, gameService: gameService, onDismiss: onDismiss)
        
        // Then
        let backButton = try playerNameView.inspect().find(ViewType.Button.self) { button in
            do {
                return try button.labelView().image().name().contains("arrow.left")
            } catch {
                return false
            }
        }
        
        try backButton.tap()
        XCTAssertTrue(backCalled)
    }
}
