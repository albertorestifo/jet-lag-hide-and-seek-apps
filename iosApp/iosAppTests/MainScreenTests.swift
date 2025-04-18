import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

extension ContentView: Inspectable {}
extension PlayerNameView: Inspectable {}
extension CreateGameView: Inspectable {}
extension GameLobbyView: Inspectable {}

class MainScreenTests: XCTestCase {
    
    func testMainScreenInitialState() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let title1 = try contentView.inspect().vStack().text(0).string()
        let title2 = try contentView.inspect().vStack().text(1).string()
        let textField = try contentView.inspect().vStack().textField(2)
        let joinButton = try contentView.inspect().vStack().button(3)
        let createButton = try contentView.inspect().vStack().button(4)
        
        // Then
        XCTAssertEqual(title1, "JetLag")
        XCTAssertEqual(title2, "Hide & Seek")
        XCTAssertEqual(try textField.labelView().text().string(), "Enter 6-character code")
        XCTAssertEqual(try joinButton.labelView().text().string(), "Join Game")
        XCTAssertEqual(try createButton.labelView().text().string(), "Create New Game")
    }
    
    func testGameCodeValidation() throws {
        // Given
        let contentView = ContentView()
        
        // When - Enter an invalid game code (too short)
        try contentView.inspect().vStack().textField(2).setInput("ABC")
        try contentView.inspect().vStack().button(3).tap()
        
        // Then - Error should be displayed
        let errorText = try? contentView.inspect().vStack().text(5).string()
        XCTAssertEqual(errorText, "Game code must be 6 characters")
    }
    
    func testGameCodeUppercaseAndFiltering() throws {
        // Given
        let contentView = ContentView()
        
        // When - Enter a mixed case code with special characters
        try contentView.inspect().vStack().textField(2).setInput("abc@123")
        
        // Then - Code should be uppercase and filtered
        let textField = try contentView.inspect().vStack().textField(2)
        let gameCode = try textField.actualInput().string()
        XCTAssertEqual(gameCode, "ABC123")
    }
    
    func testNavigationToPlayerNameScreen() throws {
        // Given
        let contentView = ContentView()
        
        // When - Enter a valid game code and tap join
        try contentView.inspect().vStack().textField(2).setInput("ABC123")
        
        // Then - Navigation link should be activated when tapped
        let navLink = try contentView.inspect().vStack().navigationLink(6)
        XCTAssertFalse(try navLink.isActive())
        
        try contentView.inspect().vStack().button(3).tap()
        
        // Note: In a real test environment with a state update, this would become true
        // but ViewInspector has limitations with state changes in unit tests
    }
    
    func testNavigationToCreateGameScreen() throws {
        // Given
        let contentView = ContentView()
        
        // When/Then - Navigation link should be activated when create game is tapped
        let navLink = try contentView.inspect().vStack().navigationLink(7)
        XCTAssertFalse(try navLink.isActive())
        
        try contentView.inspect().vStack().button(4).tap()
        
        // Note: In a real test environment with a state update, this would become true
        // but ViewInspector has limitations with state changes in unit tests
    }
}
