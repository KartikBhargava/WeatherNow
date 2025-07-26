package bhargava.kartik.weatherdashboard.utils

import bhargava.kartik.weatherdashboard.domain.model.Weather
import bhargava.kartik.weatherdashboard.presentation.viewmodel.TemperatureUnit
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WindSpeedUnit

// Extension functions to format weather data based on user settings
fun Double.formatTemperature(unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.CELSIUS -> "${this.toInt()}°C"
        TemperatureUnit.FAHRENHEIT -> "${(this * 9 / 5 + 32).toInt()}°F"
    }
}

fun Double.formatWindSpeed(unit: WindSpeedUnit): String {
    return when (unit) {
        WindSpeedUnit.METERS_PER_SECOND -> "${this.toInt()} m/s"
        WindSpeedUnit.KILOMETERS_PER_HOUR -> "${(this * 3.6).toInt()} km/h"
        WindSpeedUnit.MILES_PER_HOUR -> "${(this * 2.237).toInt()} mph"
    }
}

// Extension function for Weather to get formatted strings
fun Weather.getFormattedTemperature(unit: TemperatureUnit): String {
    return this.temperature.formatTemperature(unit)
}

fun Weather.getFormattedFeelsLike(unit: TemperatureUnit): String {
    return this.feelsLike.formatTemperature(unit)
}

fun Weather.getFormattedWindSpeed(unit: WindSpeedUnit): String {
    return this.windSpeed.formatWindSpeed(unit)
}

fun Weather.getFormattedTemperatureShort(unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.CELSIUS -> "${this.temperature.toInt()}°"
        TemperatureUnit.FAHRENHEIT -> "${(this.temperature * 9 / 5 + 32).toInt()}°"
    }
}