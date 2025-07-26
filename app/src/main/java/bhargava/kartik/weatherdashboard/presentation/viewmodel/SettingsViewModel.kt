// Update: presentation/viewmodel/SettingsViewModel.kt
package bhargava.kartik.weatherdashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bhargava.kartik.weatherdashboard.data.preferences.SettingsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TemperatureUnit(val displayName: String) {
    CELSIUS("Celsius"),
    FAHRENHEIT("Fahrenheit")
}

enum class WindSpeedUnit(val displayName: String, val symbol: String) {
    METERS_PER_SECOND("m/s", "m/s"),
    KILOMETERS_PER_HOUR("km/h", "km/h"),
    MILES_PER_HOUR("mph", "mph")
}

data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METERS_PER_SECOND,
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val appVersion: String = "1.0.0",
    val showResetDialog: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsProvider: SettingsProvider
) : ViewModel() {

    private val _showResetDialog = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsProvider.temperatureUnit,
        settingsProvider.windSpeedUnit,
        settingsProvider.notificationsEnabled,
        settingsProvider.locationEnabled,
        settingsProvider.darkModeEnabled,
        _showResetDialog,
        _isLoading
    ) { flows ->
        val temperatureUnit = flows[0] as TemperatureUnit
        val windSpeedUnit = flows[1] as WindSpeedUnit
        val notifications = flows[2] as Boolean
        val location = flows[3] as Boolean
        val darkMode = flows[4] as Boolean
        val showDialog = flows[5] as Boolean
        val loading = flows[6] as Boolean

        SettingsUiState(
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit,
            notificationsEnabled = notifications,
            locationEnabled = location,
            darkModeEnabled = darkMode,
            showResetDialog = showDialog,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsProvider.setTemperatureUnit(unit)
        }
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            settingsProvider.setWindSpeedUnit(unit)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsProvider.setNotifications(enabled)
        }
    }

    fun toggleLocationAccess(enabled: Boolean) {
        viewModelScope.launch {
            settingsProvider.setLocationAccess(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsProvider.setDarkMode(enabled)
        }
    }

    fun showResetDialog() {
        _showResetDialog.value = true
    }

    fun hideResetDialog() {
        _showResetDialog.value = false
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                settingsProvider.resetAllSettings()
                hideResetDialog()
            } catch (e: Exception) {
                // Handle error resetting settings
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Helper functions to get string values for UI
    fun getTemperatureUnitOptions(): List<String> {
        return TemperatureUnit.entries.map { it.displayName }
    }

    fun getWindSpeedUnitOptions(): List<String> {
        return WindSpeedUnit.entries.map { it.displayName }
    }

    fun updateTemperatureUnitByName(unitName: String) {
        val unit = TemperatureUnit.entries.find { it.displayName == unitName }
        if (unit != null) {
            updateTemperatureUnit(unit)
        }
    }

    fun updateWindSpeedUnitByName(unitName: String) {
        val unit = WindSpeedUnit.entries.find { it.displayName == unitName }
        if (unit != null) {
            updateWindSpeedUnit(unit)
        }
    }
}