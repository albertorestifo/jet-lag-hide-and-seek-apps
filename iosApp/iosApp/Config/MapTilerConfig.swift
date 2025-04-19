import Foundation

/// Configuration for MapTiler
struct MapTilerConfig {
    /// The MapTiler API key
    static var apiKey: String = ""
    
    /// Initialize the MapTiler configuration with the API key
    static func initialize(apiKey: String) {
        self.apiKey = apiKey
    }
}
