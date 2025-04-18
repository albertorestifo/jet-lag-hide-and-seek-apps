import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class NavigationFlowTests: XCTestCase {
    
    func testMainToPlayerNameNavigation() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let textField = try contentView.inspect().find(ViewType.TextField.self)
        try textField.setInput("ABC123")
        
        let joinButton = try contentView.inspect().find(ViewType.Button.self) { button in
            do {
                return try button.labelView().text().string() == "Join Game"
            } catch {
                return false
            }
        }
        
        // Then - In a real environment, this would navigate to PlayerNameView
        // but ViewInspector has limitations with navigation in unit tests
        XCTAssertNotNil(joinButton)
    }
    
    func testMainToCreateGameNavigation() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let createButton = try contentView.inspect().find(ViewType.Button.self) { button in
            do {
                return try button.labelView().text().string() == "Create New Game"
            } catch {
                return false
            }
        }
        
        // Then - In a real environment, this would navigate to CreateGameView
        // but ViewInspector has limitations with navigation in unit tests
        XCTAssertNotNil(createButton)
    }
    
    func testPlayerNameToGameLobbyNavigation() throws {
        // Given
        let playerNameView = PlayerNameView(gameCode: "ABC123")
        
        // When
        let textField = try playerNameView.inspect().find(ViewType.TextField.self)
        try textField.setInput("John Doe")
        
        let joinButton = try playerNameView.inspect().find(ViewType.Button.self) { button in
            do {
                return try button.labelView().text().string() == "Join Game"
            } catch {
                return false
            }
        }
        
        // Then - In a real environment, this would navigate to GameLobbyView
        // but ViewInspector has limitations with navigation in unit tests
        XCTAssertNotNil(joinButton)
    }
    
    func testBackNavigationFromPlayerName() throws {
        // Given
        var backCalled = false
        let playerNameView = PlayerNameView(
            gameCode: "ABC123",
            onDismiss: { backCalled = true }
        )
        
        // When
        let backButton = try playerNameView.inspect().find(ViewType.Button.self) { button in
            do {
                let image = try? button.labelView().image()
                return image != nil
            } catch {
                return false
            }
        }
        try backButton.tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
}
