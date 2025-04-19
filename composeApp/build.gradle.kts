import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

// Get MapTiler API key from environment variable
val mapTilerApiKey = System.getenv("MAPTILER_API_KEY") ?: ""

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.maplibre.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "dev.restifo.hide_and_seek"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "dev.restifo.hide_and_seek"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("Boolean", "IS_DEVELOPMENT", "true")
            // Add MapTiler API key as a build config field
            buildConfigField("String", "MAPTILER_API_KEY", "\"${mapTilerApiKey}\"")
            manifestPlaceholders["MAPTILER_API_KEY"] = mapTilerApiKey
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("Boolean", "IS_DEVELOPMENT", "false")
            // Add MapTiler API key as a build config field
            buildConfigField("String", "MAPTILER_API_KEY", "\"${mapTilerApiKey}\"")
            manifestPlaceholders["MAPTILER_API_KEY"] = mapTilerApiKey
        }
    }

    dependencies {
        testImplementation(libs.kotlin.test)
        testImplementation(libs.kotlin.test.junit)
        testImplementation(libs.junit)
        testImplementation(libs.androidx.test.junit)
        testImplementation(libs.androidx.espresso.core)
        testImplementation(libs.compose.ui.test)
        testImplementation(libs.robolectric)
        testImplementation(libs.mockk)
        testImplementation(libs.kotlinx.coroutines.test)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

