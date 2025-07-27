package bhargava.kartik.weatherdashboard.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bhargava.kartik.weatherdashboard.presentation.viewmodel.SettingsViewModel

object ThemeUtils {

    // Background gradients
    fun getBackgroundGradient(isDarkMode: Boolean): List<Color> {
        return if (isDarkMode) {
            listOf(
                Color(0xFF1a1a2e), // Dark navy
                Color(0xFF16213e), // Darker blue
                Color(0xFF0f3460)  // Deep blue
            )
        } else {
            listOf(
                Color(0xFF667eea), // Your current light purple
                Color(0xFF764ba2)  // Your current dark purple
            )
        }
    }

    // Card backgrounds
    fun getCardBackground(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.08f) // Much lower opacity for dark mode
        } else {
            Color.White.copy(alpha = 0.15f) // Your current opacity
        }
    }

    fun getCardBackgroundHighlight(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.12f) // Slightly higher for selected items
        } else {
            Color.White.copy(alpha = 0.25f) // Your current highlight
        }
    }

    // Text colors
    fun getTextPrimary(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.95f) // Slightly softer white
        } else {
            Color.White // Pure white for light mode
        }
    }

    fun getTextSecondary(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.75f) // More readable in dark
        } else {
            Color.White.copy(alpha = 0.8f) // Your current
        }
    }

    fun getTextTertiary(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.6f) // Readable but subtle
        } else {
            Color.White.copy(alpha = 0.7f) // Your current
        }
    }

    // Navigation colors
    fun getNavigationBackground(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.Black.copy(alpha = 0.3f) // More prominent in dark mode
        } else {
            Color.Black.copy(alpha = 0.1f) // Your current
        }
    }

    // Button/control backgrounds
    fun getControlBackground(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.1f)
        } else {
            Color.White.copy(alpha = 0.15f)
        }
    }

    fun getControlBackgroundPressed(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color.White.copy(alpha = 0.2f)
        } else {
            Color.White.copy(alpha = 0.2f)
        }
    }
}

// Extension function for easy use
@Composable
fun isDarkMode(): Boolean {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    return settingsState.darkModeEnabled
}