import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class ContentViewTests: XCTestCase {
    
    func testContentViewInitialState() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let vStack = try contentView.inspect().find(ViewType.NavigationView.self).vStack()
        
        // Then
        XCTAssertTrue(try vStack.contains(where: { view in
            do {
                return try view.text().string() == "JetLag"
            } catch {
                return false
            }
        }))
        
        XCTAssertTrue(try vStack.contains(where: { view in
            do {
                return try view.text().string() == "Hide & Seek"
            } catch {
                return false
            }
        }))
    }
    
    func testGameCodeInput() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let textField = try contentView.inspect().find(ViewType.TextField.self)
        
        // Then
        XCTAssertEqual(try textField.labelView().text().string(), "Enter 6-character code")
    }
    
    func testJoinGameButton() throws {
        // Given
        let contentView = ContentView()
        
        // When
        let joinButton = try contentView.inspect().find(ViewType.Button.self) { button in
            do {
                return try button.labelView().text().string() == "Join Game"
            } catch {
                return false
            }
        }
        
        // Then
        XCTAssertNotNil(joinButton)
    }
    
    func testCreateGameButton() throws {
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
        
        // Then
        XCTAssertNotNil(createButton)
    }
}
