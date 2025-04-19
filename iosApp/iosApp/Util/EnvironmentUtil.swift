import Foundation

/// Utility for handling environment variables in iOS
struct EnvironmentUtil {
    /// Gets an environment variable from the process environment or from Info.plist
    /// - Parameters:
    ///   - name: The name of the environment variable
    ///   - defaultValue: The default value to return if the environment variable is not found
    /// - Returns: The value of the environment variable, or the default value if not found
    static func getEnvironmentVariable(name: String, defaultValue: String = "") -> String {
        // First, try to get from process environment
        if let value = ProcessInfo.processInfo.environment[name], !value.isEmpty {
            return value
        }
        
        // If not found, try to get from Info.plist
        if let value = Bundle.main.object(forInfoDictionaryKey: name) as? String, !value.isEmpty {
            return value
        }
        
        // Return default value if not found
        return defaultValue
    }
}
