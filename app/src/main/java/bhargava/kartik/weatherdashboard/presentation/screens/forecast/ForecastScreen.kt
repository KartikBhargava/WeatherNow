// Update: presentation/screens/forecast/ForecastScreen.kt
package bhargava.kartik.weatherdashboard.presentation.screens.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bhargava.kartik.weatherdashboard.domain.model.DailyForecast
import bhargava.kartik.weatherdashboard.domain.model.HourlyForecast
import bhargava.kartik.weatherdashboard.domain.model.WeatherForecast
import bhargava.kartik.weatherdashboard.presentation.viewmodel.ForecastViewModel
import bhargava.kartik.weatherdashboard.presentation.viewmodel.TemperatureUnit
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WindSpeedUnit
import bhargava.kartik.weatherdashboard.utils.formatTemperature
import bhargava.kartik.weatherdashboard.utils.formatWindSpeed

@Composable
fun ForecastScreen(
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    viewModel: ForecastViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weather Forecast",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = uiState.forecast?.cityName ?: "Loading...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshForecast() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh forecast",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    LoadingForecastContent()
                }
                uiState.errorMessage != null -> {
                    ErrorForecastContent(
                        message = uiState.errorMessage!!,
                        onRetryClick = { viewModel.refreshForecast() }
                    )
                }
                uiState.forecast != null -> {
                    ForecastContent(
                        forecast = uiState.forecast!!,
                        temperatureUnit = temperatureUnit,
                        windSpeedUnit = windSpeedUnit
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingForecastContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading forecast data...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ErrorForecastContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forecast Unavailable",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
fun ForecastContent(
    forecast: WeatherForecast,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hourly forecast section
        item {
            HourlyForecastSection(
                hourlyForecasts = forecast.hourlyForecasts,
                temperatureUnit = temperatureUnit
            )
        }

        // Daily forecast header
        item {
            Text(
                text = "5-Day Forecast",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Daily forecast items
        items(forecast.dailyForecasts) { dailyForecast ->
            DailyForecastCard(
                dailyForecast = dailyForecast,
                temperatureUnit = temperatureUnit,
                windSpeedUnit = windSpeedUnit,
                isToday = dailyForecast.dayName == "Today"
            )
        }
    }
}

@Composable
fun HourlyForecastSection(
    hourlyForecasts: List<HourlyForecast>,
    temperatureUnit: TemperatureUnit
) {
    Column {
        Text(
            text = "24-Hour Forecast",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hourlyForecasts) { hourlyForecast ->
                    HourlyForecastItem(
                        hourlyForecast = hourlyForecast,
                        temperatureUnit = temperatureUnit
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    hourlyForecast: HourlyForecast,
    temperatureUnit: TemperatureUnit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = hourlyForecast.hour,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = getWeatherEmoji(hourlyForecast.condition),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = hourlyForecast.temperature.formatTemperature(temperatureUnit),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (hourlyForecast.precipitationChance > 0) {
            Text(
                text = "${hourlyForecast.precipitationChance}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DailyForecastCard(
    dailyForecast: DailyForecast,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    isToday: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) {
                Color.White.copy(alpha = 0.25f)
            } else {
                Color.White.copy(alpha = 0.15f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dailyForecast.dayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = formatDate(dailyForecast.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Weather icon and condition
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getWeatherEmoji(dailyForecast.condition),
                    fontSize = 32.sp
                )
                Text(
                    text = dailyForecast.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Temperature and details
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dailyForecast.maxTemp.formatTemperature(temperatureUnit),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "/${dailyForecast.minTemp.formatTemperature(temperatureUnit)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Additional info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dailyForecast.precipitationChance > 0) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${dailyForecast.precipitationChance}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Icon(
                        Icons.Default.Air,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = dailyForecast.windSpeed.formatWindSpeed(windSpeedUnit),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// Helper functions
private fun getWeatherEmoji(condition: String): String {
    return when (condition.lowercase()) {
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "rain" -> "🌧️"
        "drizzle" -> "🌦️"
        "thunderstorm" -> "⛈️"
        "snow" -> "❄️"
        "mist", "fog" -> "🌫️"
        else -> "🌤️"
    }
}

private fun formatDate(dateString: String): String {
    val parts = dateString.split("-")
    if (parts.size == 3) {
        val month = when (parts[1]) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Aug"
            "09" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"
            else -> parts[1]
        }
        return "$month ${parts[2]}"
    }
    return dateString
}

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        // Preview content
    }
}