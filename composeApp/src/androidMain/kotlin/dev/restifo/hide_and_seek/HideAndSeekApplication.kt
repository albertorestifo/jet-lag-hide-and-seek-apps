package dev.restifo.hide_and_seek

import android.app.Application
import dev.restifo.hide_and_seek.persistence.AndroidContextProvider

/**
 * Android application class for Hide and Seek.
 */
class HideAndSeekApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the Android context provider
        AndroidContextProvider.init(this)
        
        // Initialize the app with the correct environment
        HideAndSeekApp.initialize(BuildConfig.DEBUG)
    }
}
