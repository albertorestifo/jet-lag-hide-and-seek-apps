package dev.restifo.hide_and_seek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import dev.restifo.hide_and_seek.config.MapConfig
import dev.restifo.hide_and_seek.game.GameService
import dev.restifo.hide_and_seek.util.EnvironmentUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set MapTiler API key from environment variable
        val apiKey = System.getenv(MapConfig.ENV_MAPTILER_API_KEY) ?: ""

        if (apiKey.isEmpty()) {
            // Log error or show a message to the user
            println("WARNING: MapTiler API key not found. Maps will not work correctly.")
            println("Please set the MAPTILER_API_KEY environment variable or in local.properties.")
        }

        MapConfig.setApiKey(apiKey)

        // Restore connection if available
        if (savedInstanceState == null) {
            // Only restore on fresh start, not configuration change
            lifecycleScope.launch {
                GameService.getInstance().restoreConnection()
            }
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}