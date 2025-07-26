// Update: presentation/screens/locations/LocationsScreen.kt
package bhargava.kartik.weatherdashboard.presentation.screens.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import bhargava.kartik.weatherdashboard.presentation.viewmodel.LocationsViewModel
import bhargava.kartik.weatherdashboard.presentation.viewmodel.LocationWithWeather
import bhargava.kartik.weatherdashboard.presentation.viewmodel.TemperatureUnit
import bhargava.kartik.weatherdashboard.presentation.viewmodel.WindSpeedUnit
import bhargava.kartik.weatherdashboard.utils.formatTemperature
import bhargava.kartik.weatherdashboard.utils.formatWindSpeed

@Composable
fun LocationsScreen(
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    viewModel: LocationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

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
            // Header with add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Locations",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${uiState.locations.size} saved locations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.size(56.dp),
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add location")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Locations list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.locations) { locationWithWeather ->
                    LocationCard(
                        locationWithWeather = locationWithWeather,
                        temperatureUnit = temperatureUnit,
                        windSpeedUnit = windSpeedUnit,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(locationWithWeather.location.id)
                        },
                        onDeleteClick = {
                            viewModel.removeLocation(locationWithWeather.location.id)
                        },
                        onSetDefaultClick = {
                            viewModel.setDefaultLocation(locationWithWeather.location.id)
                        }
                    )
                }

                // Add location prompt
                item {
                    AddLocationCard(
                        onClick = { showAddDialog = true }
                    )
                }
            }
        }

        // Add location dialog
        if (showAddDialog) {
            AddLocationDialog(
                isLoading = uiState.isAddingLocation,
                onDismiss = { showAddDialog = false },
                onAddLocation = { cityName ->
                    viewModel.addLocation(cityName)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun LocationCard(
    locationWithWeather: LocationWithWeather,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSetDefaultClick: () -> Unit
) {
    val location = locationWithWeather.location
    val weather = locationWithWeather.weather

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSetDefaultClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (location.isDefault) {
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
            // Location info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (location.isDefault) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Default location",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Weather info or loading state
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    locationWithWeather.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    }
                    locationWithWeather.hasError -> {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error loading weather",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    weather != null -> {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getWeatherIcon(weather.description),
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))

                                // Use the utility formatting functions
                                Text(
                                    text = weather.temperature.formatTemperature(temperatureUnit),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Show wind speed if available
                            if (weather.windSpeed != null) {
                                Text(
                                    text = weather.windSpeed.formatWindSpeed(windSpeedUnit),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            Text(
                                text = weather.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Action buttons
                Column {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            if (location.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (location.isFavorite) Color.Red.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!location.isDefault) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete location",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddLocationCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add location",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add New Location",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun AddLocationDialog(
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onAddLocation: (String) -> Unit
) {
    var cityName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text("Add Location", color = Color.Black)
        },
        text = {
            Column {
                Text("Enter city name:", color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("City name") },
                    placeholder = { Text("e.g. London, Paris, Tokyo") },
                    singleLine = true,
                    enabled = !isLoading
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adding location...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (cityName.isNotBlank()) {
                        onAddLocation(cityName.trim())
                    }
                },
                enabled = cityName.isNotBlank() && !isLoading
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

// Helper function to get weather icon
private fun getWeatherIcon(description: String): String {
    return when (description.lowercase()) {
        "sunny", "clear" -> "☀️"
        "partly cloudy", "partly sunny" -> "⛅"
        "cloudy", "overcast" -> "☁️"
        "rainy", "rain" -> "🌧️"
        "stormy", "thunderstorm" -> "⛈️"
        "snowy", "snow" -> "❄️"
        "foggy", "mist" -> "🌫️"
        else -> "🌤️"
    }
}

@Preview(showBackground = true)
@Composable
fun LocationsScreenPreview() {
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