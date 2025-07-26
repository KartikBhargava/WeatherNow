package bhargava.kartik.weatherdashboard.data.remote.dto

import bhargava.kartik.weatherdashboard.domain.model.DailyForecast
import bhargava.kartik.weatherdashboard.domain.model.HourlyForecast
import bhargava.kartik.weatherdashboard.domain.model.Weather
import bhargava.kartik.weatherdashboard.domain.model.WeatherForecast
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherInfo>,
    val wind: Wind,
    val visibility: Int,
    val dt: Long
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Double
)

data class WeatherInfo(
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    @SerializedName("dt_txt")
    val dateTime: String,
    val main: Main,
    val weather: List<WeatherInfo>,
    val wind: Wind,
    val visibility: Int,
    val pop: Double // Probability of precipitation
)

data class City(
    val name: String,
    val country: String,
    val timezone: Int
)

fun WeatherResponse.toWeather(): Weather {
    return Weather(
        locationName = name,
        temperature = main.temp,
        description = weather.firstOrNull()?.description ?: "",
        humidity = main.humidity,
        windSpeed = wind.speed,
        pressure = main.pressure,
        feelsLike = main.feelsLike,
        iconCode = weather.firstOrNull()?.icon ?: "",
        timestamp = dt * 1000 // Convert to milliseconds
    )
}

fun ForecastResponse.toWeatherForecast(): WeatherForecast {
    // Group forecast items by date
    val groupedByDate = list.groupBy { item ->
        item.dateTime.substring(0, 10) // Extract date (YYYY-MM-DD)
    }

    // Create daily forecasts
    val dailyForecasts = groupedByDate.map { (date, items) ->
        val maxTemp = items.maxOf { it.main.temp }
        val minTemp = items.minOf { it.main.temp }
        val avgHumidity = items.map { it.main.humidity }.average().toInt()
        val avgWindSpeed = items.map { it.wind.speed }.average()
        val avgPrecipitation = items.map { it.pop * 100 }.average().toInt()

        // Use the middle item for general condition
        val middleItem = items[items.size / 2]

        DailyForecast(
            date = date,
            dayName = formatDayName(items.first().dt),
            maxTemp = maxTemp,
            minTemp = minTemp,
            condition = middleItem.weather.firstOrNull()?.main ?: "Unknown",
            description = middleItem.weather.firstOrNull()?.description ?: "Unknown",
            humidity = avgHumidity,
            windSpeed = avgWindSpeed,
            precipitationChance = avgPrecipitation,
            iconCode = middleItem.weather.firstOrNull()?.icon ?: "01d"
        )
    }.take(5) // Take only 5 days

    // Create hourly forecasts (next 24 hours)
    val hourlyForecasts = list.take(8).map { item -> // 8 items = 24 hours (3-hour intervals)
        HourlyForecast(
            time = item.dateTime,
            hour = formatHour(item.dt),
            temperature = item.main.temp,
            condition = item.weather.firstOrNull()?.main ?: "Unknown",
            iconCode = item.weather.firstOrNull()?.icon ?: "01d",
            precipitationChance = (item.pop * 100).toInt(),
            windSpeed = item.wind.speed
        )
    }

    return WeatherForecast(
        cityName = city.name,
        dailyForecasts = dailyForecasts,
        hourlyForecasts = hourlyForecasts
    )
}

private fun formatDayName(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000

    val today = java.util.Calendar.getInstance()
    val tomorrow = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }

    return when {
        calendar.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR) -> "Today"
        calendar.get(java.util.Calendar.DAY_OF_YEAR) == tomorrow.get(java.util.Calendar.DAY_OF_YEAR) -> "Tomorrow"
        else -> {
            val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            days[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
        }
    }
}

private fun formatHour(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    return if (hour == 0) "12 AM" else if (hour < 12) "$hour AM" else if (hour == 12) "12 PM" else "${hour - 12} PM"
}