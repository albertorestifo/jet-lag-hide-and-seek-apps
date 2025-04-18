import SwiftUI
import Shared

class CreateGameViewModel: ObservableObject {
    private let geocodingService = GeocodingService.Companion().getInstance()

    @Published var searchQuery = ""
    @Published var searchResults: [LocationSearchResult] = []
    @Published var selectedLocation: LocationSearchResult? = nil
    @Published var locationBoundaries: LocationBoundaries? = nil
    @Published var isSearching = false
    @Published var isLoadingBoundaries = false
    @Published var errorMessage: String? = nil

    private var searchTask: Task<Void, Never>? = nil

    func updateSearchQuery(_ query: String) {
        searchQuery = query

        if query.isEmpty {
            searchResults = []
            isSearching = false
            return
        }

        // Cancel previous search task
        searchTask?.cancel()

        // Debounce search
        isSearching = true
        searchTask = Task { [weak self] in
            // Debounce for 300ms
            try? await Task.sleep(nanoseconds: 300_000_000)

            guard !Task.isCancelled else { return }

            await self?.searchLocations(query: query)
        }
    }

    @MainActor
    private func searchLocations(query: String) async {
        do {
            let results = try await geocodingService.searchLocations(query: query, limit: 10)
            searchResults = results
            isSearching = false
        } catch {
            searchResults = []
            isSearching = false
            errorMessage = "Error searching locations: \(error.localizedDescription)"
        }
    }

    func selectLocation(_ location: LocationSearchResult) {
        selectedLocation = location
        searchQuery = location.title
        searchResults = []
        isLoadingBoundaries = true

        Task { [weak self] in
            await self?.loadLocationBoundaries(locationId: location.id)
        }
    }

    @MainActor
    private func loadLocationBoundaries(locationId: String) async {
        do {
            let boundaries = try await geocodingService.getLocationBoundaries(locationId: locationId)
            locationBoundaries = boundaries
            isLoadingBoundaries = false
        } catch {
            isLoadingBoundaries = false
            errorMessage = "Error loading location boundaries: \(error.localizedDescription)"
        }
    }

    func clearSelectedLocation() {
        selectedLocation = nil
        locationBoundaries = nil
    }

    func clearError() {
        errorMessage = nil
    }
}

struct CreateGameScreen: View {
    var onBack: () -> Void
    @StateObject private var viewModel = CreateGameViewModel()
    @State private var showingError = false

    var body: some View {
        VStack(spacing: 0) {
            // Top bar with back button
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 20))
                        .foregroundColor(.blue)
                }

                Text("Create New Game")
                    .font(.headline)
                    .padding(.leading, 8)

                Spacer()
            }
            .padding(.bottom, 24)

            // Content based on current state
            if let selectedLocation = viewModel.selectedLocation,
               let locationBoundaries = viewModel.locationBoundaries {
                // Location confirmation screen
                LocationConfirmationView(
                    locationName: selectedLocation.title,
                    locationSubtitle: selectedLocation.subtitle,
                    onConfirm: { /* Will be implemented in next step */ },
                    onBack: { viewModel.clearSelectedLocation() },
                    isLoading: viewModel.isLoadingBoundaries
                )
            } else {
                // Location search screen
                LocationSearchView(
                    searchQuery: $viewModel.searchQuery,
                    onSearchQueryChange: { viewModel.updateSearchQuery($0) },
                    searchResults: viewModel.searchResults,
                    onLocationSelected: { viewModel.selectLocation($0) },
                    isSearching: viewModel.isSearching,
                    onClearSearch: { viewModel.updateSearchQuery("") }
                )
            }

            Spacer()
        }
        .padding()
        .navigationBarHidden(true)
        .alert(isPresented: $showingError, content: {
            Alert(
                title: Text("Error"),
                message: Text(viewModel.errorMessage ?? "An unknown error occurred"),
                dismissButton: .default(Text("OK")) {
                    viewModel.clearError()
                }
            )
        })
        .onChange(of: viewModel.errorMessage) { newValue in
            showingError = newValue != nil
        }
    }
}

struct LocationSearchView: View {
    @Binding var searchQuery: String
    var onSearchQueryChange: (String) -> Void
    var searchResults: [LocationSearchResult]
    var onLocationSelected: (LocationSearchResult) -> Void
    var isSearching: Bool
    var onClearSearch: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Select Game Area")
                .font(.title)
                .fontWeight(.bold)

            Text("Enter a city, region, or country name")
                .font(.body)

            // Search box
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)

                TextField("Search locations...", text: $searchQuery)
                    .onChange(of: searchQuery) { newValue in
                        onSearchQueryChange(newValue)
                    }

                if !searchQuery.isEmpty {
                    Button(action: onClearSearch) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                }
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(10)

            // Loading indicator or results
            if isSearching {
                HStack {
                    Spacer()
                    ProgressView()
                    Spacer()
                }
                .padding(.top, 20)
            } else if searchResults.isEmpty && !searchQuery.isEmpty {
                Text("No locations found")
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.top, 20)
            } else {
                ScrollView {
                    LazyVStack(spacing: 8) {
                        ForEach(searchResults, id: \.id) { location in
                            LocationSearchResultItem(
                                location: location,
                                onClick: { onLocationSelected(location) }
                            )
                        }
                    }
                }
            }
        }
    }
}

struct LocationSearchResultItem: View {
    var location: LocationSearchResult
    var onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 4) {
                Text(location.title)
                    .font(.headline)
                    .fontWeight(.bold)
                    .foregroundColor(.primary)

                Text(location.subtitle)
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(8)
            .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
        }
    }
}

struct LocationConfirmationView: View {
    var locationName: String
    var locationSubtitle: String
    var onConfirm: () -> Void
    var onBack: () -> Void
    var isLoading: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Confirm Location")
                .font(.title)
                .fontWeight(.bold)

            // Location info
            VStack(alignment: .leading, spacing: 4) {
                Text(locationName)
                    .font(.headline)
                    .fontWeight(.bold)

                Text(locationSubtitle)
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(8)
            .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)

            // Map placeholder
            ZStack {
                Rectangle()
                    .fill(Color(.systemGray5))
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.systemGray3), lineWidth: 1)
                    )

                if isLoading {
                    ProgressView()
                } else {
                    Text("Map will be displayed here")
                        .foregroundColor(.gray)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 300)

            Spacer()

            // Action buttons
            HStack(spacing: 16) {
                Button(action: onBack) {
                    Text("Back")
                        .font(.headline)
                        .foregroundColor(.blue)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.blue, lineWidth: 2)
                        )
                }

                Button(action: onConfirm) {
                    Text("Confirm")
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(8)
                }
                .disabled(isLoading)
            }
        }
    }
}

#Preview {
    CreateGameScreen(onBack: { })
}
