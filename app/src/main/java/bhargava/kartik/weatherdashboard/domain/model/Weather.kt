package bhargava.kartik.weatherdashboard.domain.model

data class Weather(
    val locationName: String,
    val temperature: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val feelsLike: Double,
    val iconCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
