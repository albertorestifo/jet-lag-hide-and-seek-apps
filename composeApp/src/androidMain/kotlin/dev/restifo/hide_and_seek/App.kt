package dev.restifo.hide_and_seek

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.restifo.hide_and_seek.ui.navigation.AppNavigation
import dev.restifo.hide_and_seek.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        AppNavigation()
    }
}