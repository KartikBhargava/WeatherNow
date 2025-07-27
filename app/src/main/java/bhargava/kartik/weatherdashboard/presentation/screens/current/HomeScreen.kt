package bhargava.kartik.weatherdashboard.presentation.screens.current

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bhargava.kartik.weatherdashboard.domain.model.Weather
import bhargava.kartik.weatherdashboard.presentation.viewmodel.SettingsViewModel
import bhargava.kartik.weatherdashboard.presentation.viewmodel.TemperatureUnit
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WeatherViewModel
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WindSpeedUnit
import bhargava.kartik.weatherdashboard.utils.ThemeUtils
import bhargava.kartik.weatherdashboard.utils.formatTemperature
import bhargava.kartik.weatherdashboard.utils.formatWindSpeed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Get dark mode state
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = settingsState.darkModeEnabled

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = ThemeUtils.getBackgroundGradient(isDarkMode)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with location and refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Weather",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ThemeUtils.getTextPrimary(isDarkMode)
                    )
                    Text(
                        text = uiState.weather?.locationName ?: "Loading...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ThemeUtils.getTextSecondary(isDarkMode)
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshWeather() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            ThemeUtils.getControlBackground(isDarkMode),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh weather",
                        tint = ThemeUtils.getTextPrimary(isDarkMode),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    LoadingContent(isDarkMode)
                }
                uiState.errorMessage != null -> {
                    ErrorContent(
                        message = uiState.errorMessage!!,
                        isDarkMode = isDarkMode,
                        onRetryClick = { viewModel.refreshWeather() }
                    )
                }
                uiState.weather != null -> {
                    WeatherContent(
                        weather = uiState.weather!!,
                        temperatureUnit = temperatureUnit,
                        windSpeedUnit = windSpeedUnit,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingContent(isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ThemeUtils.getCardBackground(isDarkMode)
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
                    color = ThemeUtils.getTextPrimary(isDarkMode),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading weather data...",
                    color = ThemeUtils.getTextSecondary(isDarkMode),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    isDarkMode: Boolean,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ThemeUtils.getCardBackground(isDarkMode)
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
                text = "Weather Unavailable",
                style = MaterialTheme.typography.titleLarge,
                color = ThemeUtils.getTextPrimary(isDarkMode),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeUtils.getTextSecondary(isDarkMode)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeUtils.getControlBackgroundPressed(isDarkMode)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", color = ThemeUtils.getTextPrimary(isDarkMode))
            }
        }
    }
}

@Composable
fun WeatherContent(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    isDarkMode: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main weather card
        MainWeatherCard(
            weather = weather,
            temperatureUnit = temperatureUnit,
            isDarkMode = isDarkMode
        )

        // Weather details grid
        WeatherDetailsGrid(
            weather = weather,
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit,
            isDarkMode = isDarkMode
        )

        // Additional info card
        AdditionalInfoCard(
            weather = weather,
            isDarkMode = isDarkMode
        )
    }
}

@Composable
fun MainWeatherCard(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = ThemeUtils.getCardBackground(isDarkMode)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getWeatherEmoji(weather.description),
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = weather.temperature.formatTemperature(temperatureUnit),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = ThemeUtils.getTextPrimary(isDarkMode),
                fontSize = 48.sp
            )

            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = ThemeUtils.getTextSecondary(isDarkMode)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Feels like ${weather.feelsLike.formatTemperature(temperatureUnit)}",
                style = MaterialTheme.typography.bodyLarge,
                color = ThemeUtils.getTextTertiary(isDarkMode)
            )
        }
    }
}

@Composable
fun WeatherDetailsGrid(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Default.Air,
                title = "Wind",
                value = weather.windSpeed.formatWindSpeed(windSpeedUnit),
                isDarkMode = isDarkMode
            )

            WeatherDetailCard(
                icon = Icons.Default.WaterDrop,
                title = "Humidity",
                value = "${weather.humidity}%",
                isDarkMode = isDarkMode
            )
        }

        // Right column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Default.Speed,
                title = "Pressure",
                value = "${weather.pressure.toInt()} hPa",
                isDarkMode = isDarkMode
            )

            WeatherDetailCard(
                icon = Icons.Default.WbSunny,
                title = "Feels Like",
                value = weather.feelsLike.formatTemperature(temperatureUnit),
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
fun WeatherDetailCard(
    icon: ImageVector,
    title: String,
    value: String,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ThemeUtils.getCardBackground(isDarkMode)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = ThemeUtils.getTextSecondary(isDarkMode),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = ThemeUtils.getTextTertiary(isDarkMode)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ThemeUtils.getTextPrimary(isDarkMode)
            )
        }
    }
}

@Composable
fun AdditionalInfoCard(
    weather: Weather,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ThemeUtils.getCardBackground(isDarkMode)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Icon Code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeUtils.getTextTertiary(isDarkMode)
                )
                Text(
                    text = weather.iconCode,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ThemeUtils.getTextPrimary(isDarkMode)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Updated",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeUtils.getTextTertiary(isDarkMode)
                )
                Text(
                    text = formatTimestamp(weather.timestamp),
                    style = MaterialTheme.typography.titleMedium,
                    color = ThemeUtils.getTextPrimary(isDarkMode)
                )
            }
        }
    }
}

// Helper functions
private fun getWeatherEmoji(condition: String): String {
    return when (condition.lowercase()) {
        "clear", "sunny" -> "☀️"
        "clouds", "cloudy" -> "☁️"
        "rain", "rainy" -> "🌧️"
        "drizzle" -> "🌦️"
        "thunderstorm", "stormy" -> "⛈️"
        "snow", "snowy" -> "❄️"
        "mist", "fog", "foggy" -> "🌫️"
        else -> "🌤️"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        else -> {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            formatter.format(date)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
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
    )
}