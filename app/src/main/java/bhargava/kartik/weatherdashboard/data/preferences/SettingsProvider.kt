// Create: data/preferences/SettingsProvider.kt
package bhargava.kartik.weatherdashboard.data.preferences

import android.content.Context
import android.content.SharedPreferences
import bhargava.kartik.weatherdashboard.presentation.viewmodel.TemperatureUnit
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WindSpeedUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SettingsProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("weather_settings", Context.MODE_PRIVATE)

    private val _temperatureUnit = MutableStateFlow(getTemperatureUnit())
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(getWindSpeedUnit())
    val windSpeedUnit: StateFlow<WindSpeedUnit> = _windSpeedUnit.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(getDarkMode())
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(getNotifications())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _locationEnabled = MutableStateFlow(getLocationAccess())
    val locationEnabled: StateFlow<Boolean> = _locationEnabled.asStateFlow()

    // Getters
    private fun getTemperatureUnit(): TemperatureUnit {
        val unitName = prefs.getString("temperature_unit", TemperatureUnit.CELSIUS.name)
        return try {
            TemperatureUnit.valueOf(unitName ?: TemperatureUnit.CELSIUS.name)
        } catch (e: Exception) {
            TemperatureUnit.CELSIUS
        }
    }

    private fun getWindSpeedUnit(): WindSpeedUnit {
        val unitName = prefs.getString("wind_speed_unit", WindSpeedUnit.METERS_PER_SECOND.name)
        return try {
            WindSpeedUnit.valueOf(unitName ?: WindSpeedUnit.METERS_PER_SECOND.name)
        } catch (e: Exception) {
            WindSpeedUnit.METERS_PER_SECOND
        }
    }

    private fun getDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode_enabled", false)
    }

    private fun getNotifications(): Boolean {
        return prefs.getBoolean("notifications_enabled", true)
    }

    private fun getLocationAccess(): Boolean {
        return prefs.getBoolean("location_enabled", true)
    }

    // Setters
    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString("temperature_unit", unit.name).apply()
        _temperatureUnit.value = unit
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        prefs.edit() { putString("wind_speed_unit", unit.name) }
        _windSpeedUnit.value = unit
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit() { putBoolean("dark_mode_enabled", enabled) }
        _darkModeEnabled.value = enabled
    }

    fun setNotifications(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setLocationAccess(enabled: Boolean) {
        prefs.edit().putBoolean("location_enabled", enabled).apply()
        _locationEnabled.value = enabled
    }

    fun resetAllSettings() {
        prefs.edit().clear().apply()
        _temperatureUnit.value = TemperatureUnit.CELSIUS
        _windSpeedUnit.value = WindSpeedUnit.METERS_PER_SECOND
        _darkModeEnabled.value = false
        _notificationsEnabled.value = true
        _locationEnabled.value = true
    }
}