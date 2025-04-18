import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class CreateGameScreenTests: XCTestCase {
    
    func testCreateGameScreenInitialState() throws {
        // Given
        let createGameView = CreateGameView()
        
        // When
        let header = try createGameView.inspect().vStack().hStack(0).text(1).string()
        let title = try createGameView.inspect().vStack().vStack(1).text(0).string()
        let description = try createGameView.inspect().vStack().vStack(1).text(1).string()
        let button = try createGameView.inspect().vStack().vStack(1).button(2)
        
        // Then
        XCTAssertEqual(header, "Create New Game")
        XCTAssertEqual(title, "Game Creation Wizard")
        XCTAssertEqual(description, "This screen will be implemented in a future update.")
        XCTAssertEqual(try button.labelView().text().string(), "Go Back")
    }
    
    func testBackButtonNavigation() throws {
        // Given
        var backCalled = false
        let createGameView = CreateGameView(
            onDismiss: { backCalled = true }
        )
        
        // When
        try createGameView.inspect().vStack().hStack(0).button(0).tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
    
    func testGoBackButtonNavigation() throws {
        // Given
        var backCalled = false
        let createGameView = CreateGameView(
            onDismiss: { backCalled = true }
        )
        
        // When
        try createGameView.inspect().vStack().vStack(1).button(2).tap()
        
        // Then
        XCTAssertTrue(backCalled)
    }
}
