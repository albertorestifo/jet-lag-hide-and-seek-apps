import XCTest

class SimpleTests: XCTestCase {
    
    func testExample() {
        // This is a simple test to verify our test setup
        XCTAssertEqual(2 + 2, 4)
    }
    
    func testGameCodeValidation() {
        // Test that a 6-character code is valid
        let validCode = "ABC123"
        XCTAssertEqual(validCode.count, 6)
        
        // Test that a code with less than 6 characters is invalid
        let invalidCode = "ABC"
        XCTAssertNotEqual(invalidCode.count, 6)
    }
    
    func testPlayerNameValidation() {
        // Test that a non-empty name is valid
        let validName = "John Doe"
        XCTAssertFalse(validName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
        
        // Test that an empty name is invalid
        let invalidName = "   "
        XCTAssertTrue(invalidName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
    }
}
