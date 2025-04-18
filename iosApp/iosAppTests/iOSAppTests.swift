import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class iOSAppTests: XCTestCase {
    
    func testAppInitialization() throws {
        // Given
        let app = iOSApp()
        
        // When
        let windowGroup = try app.inspect().windowGroup()
        let contentView = try windowGroup.view(ContentView.self)
        
        // Then
        XCTAssertNotNil(contentView)
    }
}
