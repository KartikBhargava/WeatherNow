package bhargava.kartik.weatherdashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bhargava.kartik.weatherdashboard.presentation.screens.forecast.ForecastScreen
import bhargava.kartik.weatherdashboard.presentation.screens.current.HomeScreen
import bhargava.kartik.weatherdashboard.presentation.screens.locations.LocationsScreen
import bhargava.kartik.weatherdashboard.presentation.screens.settings.SettingsScreen
import bhargava.kartik.weatherdashboard.presentation.viewmodel.SettingsViewModel

@Composable
fun WeatherNavigation(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = WeatherDestination.Home.route,
        modifier = modifier
    ) {
        composable(WeatherDestination.Home.route) {
            HomeScreen(
                temperatureUnit = settingsState.temperatureUnit,
                windSpeedUnit = settingsState.windSpeedUnit
            )
        }
        composable(WeatherDestination.Forecast.route) {
            ForecastScreen(
                temperatureUnit = settingsState.temperatureUnit,
                windSpeedUnit = settingsState.windSpeedUnit
            )
        }
        composable(WeatherDestination.Locations.route) {
            LocationsScreen(
                temperatureUnit = settingsState.temperatureUnit,
                windSpeedUnit = settingsState.windSpeedUnit
            )
        }
        composable(WeatherDestination.Settings.route) {
            SettingsScreen()
        }
    }
}