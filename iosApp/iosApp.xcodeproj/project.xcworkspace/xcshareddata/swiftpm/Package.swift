// swift-tools-version:5.5
import PackageDescription

let package = Package(
    name: "iosApp",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "iosApp",
            targets: ["iosApp"]),
    ],
    dependencies: [
        .package(url: "https://github.com/nalexn/ViewInspector", from: "0.9.8"),
    ],
    targets: [
        .target(
            name: "iosApp",
            dependencies: []),
        .testTarget(
            name: "iosAppTests",
            dependencies: ["iosApp", "ViewInspector"]),
    ]
)
