package bhargava.kartik.weatherdashboard.domain.model

data class WeatherForecast(
    val cityName: String,
    val dailyForecasts: List<DailyForecast>,
    val hourlyForecasts: List<HourlyForecast>
)

data class DailyForecast(
    val date: String,
    val dayName: String,
    val maxTemp: Double,
    val minTemp: Double,
    val condition: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val precipitationChance: Int,
    val iconCode: String
)

data class HourlyForecast(
    val time: String,
    val hour: String,
    val temperature: Double,
    val condition: String,
    val iconCode: String,
    val precipitationChance: Int,
    val windSpeed: Double
)