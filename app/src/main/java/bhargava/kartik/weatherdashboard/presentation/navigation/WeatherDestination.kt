package bhargava.kartik.weatherdashboard.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WeatherDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : WeatherDestination(
        route = "home",
        title = "Current",
        icon = Icons.Default.Home
    )

    object Forecast : WeatherDestination(
        route = "forecast",
        title = "Forecast",
        icon = Icons.Default.DateRange
    )

    object Locations : WeatherDestination(
        route = "locations",
        title = "Locations",
        icon = Icons.Default.LocationOn
    )

    object Settings : WeatherDestination(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}