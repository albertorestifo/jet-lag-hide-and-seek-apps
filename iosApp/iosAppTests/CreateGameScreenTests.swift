import XCTest
import SwiftUI
import ViewInspector
@testable import iosApp

class CreateGameScreenTests: XCTestCase {

    func testCreateGameScreenInitialState() throws {
        // Given
        let createGameView = CreateGameScreen(onBack: {})

        // When
        let header = try createGameView.inspect().vStack().hStack(0).text(1).string()

        // Then
        XCTAssertEqual(header, "Create New Game")

        // Check if the LocationSearchView is displayed initially
        let locationSearchView = try createGameView.inspect().vStack().find(LocationSearchView.self)
        XCTAssertNotNil(locationSearchView)
    }

    func testBackButtonNavigation() throws {
        // Given
        var backCalled = false
        let createGameView = CreateGameScreen(onBack: { backCalled = true })

        // When
        try createGameView.inspect().vStack().hStack(0).button(0).tap()

        // Then
        XCTAssertTrue(backCalled)
    }

    func testLocationSearchView() throws {
        // Given
        let locationSearchView = LocationSearchView(
            searchQuery: .constant(""),
            onSearchQueryChange: { _ in },
            searchResults: [],
            onLocationSelected: { _ in },
            isSearching: false,
            onClearSearch: {}
        )

        // When
        let title = try locationSearchView.inspect().vStack().text(0).string()
        let subtitle = try locationSearchView.inspect().vStack().text(1).string()

        // Then
        XCTAssertEqual(title, "Select Game Area")
        XCTAssertEqual(subtitle, "Enter a city, region, or country name")
    }

    func testLocationConfirmationView() throws {
        // Given
        let locationConfirmationView = LocationConfirmationView(
            locationName: "Madrid",
            locationSubtitle: "City",
            onConfirm: {},
            onBack: {},
            isLoading: false
        )

        // When
        let title = try locationConfirmationView.inspect().vStack().text(0).string()
        let locationName = try locationConfirmationView.inspect().vStack().vStack(1).vStack().text(0).string()
        let locationSubtitle = try locationConfirmationView.inspect().vStack().vStack(1).vStack().text(1).string()

        // Then
        XCTAssertEqual(title, "Confirm Location")
        XCTAssertEqual(locationName, "Madrid")
        XCTAssertEqual(locationSubtitle, "City")
    }
}
