// Create: presentation/viewmodel/LocationsViewModel.kt
package bhargava.kartik.weatherdashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bhargava.kartik.weatherdashboard.data.repository.WeatherRepository
import bhargava.kartik.weatherdashboard.domain.model.Location
import bhargava.kartik.weatherdashboard.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationWithWeather(
    val location: Location,
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

data class LocationsUiState(
    val locations: List<LocationWithWeather> = emptyList(),
    val isAddingLocation: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadInitialLocations()
    }

    private fun loadInitialLocations() {
        val defaultLocations = listOf(
            Location(1, "London", "United Kingdom", 51.5074, -0.1278, true),
            Location(2, "New York", "United States", 40.7128, -74.0060, false),
            Location(3, "Tokyo", "Japan", 35.6762, 139.6503, false),
            Location(4, "Paris", "France", 48.8566, 2.3522, false)
        )

        val locationsWithWeather = defaultLocations.map { location ->
            LocationWithWeather(location = location, isLoading = true)
        }

        _uiState.value = _uiState.value.copy(locations = locationsWithWeather)

        // Load weather for each location
        defaultLocations.forEach { location ->
            loadWeatherForLocation(location)
        }
    }

    private fun loadWeatherForLocation(location: Location) {
        viewModelScope.launch {
            repository.getCurrentWeatherByCoordinates(location.latitude, location.longitude)
                .onSuccess { weather ->
                    updateLocationWeather(location.id, weather, false, false)
                }
                .onFailure {
                    updateLocationWeather(location.id, null, false, true)
                }
        }
    }

    private fun updateLocationWeather(
        locationId: Int,
        weather: Weather?,
        isLoading: Boolean,
        hasError: Boolean
    ) {
        val currentLocations = _uiState.value.locations.toMutableList()
        val index = currentLocations.indexOfFirst { it.location.id == locationId }

        if (index != -1) {
            currentLocations[index] = currentLocations[index].copy(
                weather = weather,
                isLoading = isLoading,
                hasError = hasError
            )
            _uiState.value = _uiState.value.copy(locations = currentLocations)
        }
    }

    fun addLocation(cityName: String) {
        if (cityName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingLocation = true, errorMessage = null)

            repository.getCurrentWeather(cityName.trim())
                .onSuccess { weather ->
                    val newLocation = Location(
                        id = (_uiState.value.locations.maxOfOrNull { it.location.id } ?: 0) + 1,
                        name = weather.locationName,
                        latitude = 0.0, // We would get this from geocoding API
                        longitude = 0.0,
                        isDefault = false
                    )

                    val newLocationWithWeather = LocationWithWeather(
                        location = newLocation,
                        weather = weather,
                        isLoading = false,
                        hasError = false
                    )

                    val updatedLocations = _uiState.value.locations + newLocationWithWeather
                    _uiState.value = _uiState.value.copy(
                        locations = updatedLocations,
                        isAddingLocation = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isAddingLocation = false,
                        errorMessage = "Could not find weather data for '$cityName'. Please check the city name."
                    )
                }
        }
    }

    fun removeLocation(locationId: Int) {
        val updatedLocations = _uiState.value.locations.filter { it.location.id != locationId }
        _uiState.value = _uiState.value.copy(locations = updatedLocations)
    }

    fun toggleFavorite(locationId: Int) {
        val currentLocations = _uiState.value.locations.toMutableList()
        val index = currentLocations.indexOfFirst { it.location.id == locationId }

        if (index != -1) {
            val currentLocation = currentLocations[index].location
            val updatedLocation = currentLocation.copy(isFavorite = !currentLocation.isFavorite)
            currentLocations[index] = currentLocations[index].copy(location = updatedLocation)
            _uiState.value = _uiState.value.copy(locations = currentLocations)
        }
    }

    fun setDefaultLocation(locationId: Int) {
        val currentLocations = _uiState.value.locations.map { locationWithWeather ->
            val updatedLocation = locationWithWeather.location.copy(
                isDefault = locationWithWeather.location.id == locationId
            )
            locationWithWeather.copy(location = updatedLocation)
        }
        _uiState.value = _uiState.value.copy(locations = currentLocations)
    }

    fun refreshAllLocations() {
        val currentLocations = _uiState.value.locations.map { it.copy(isLoading = true) }
        _uiState.value = _uiState.value.copy(locations = currentLocations)

        _uiState.value.locations.forEach { locationWithWeather ->
            loadWeatherForLocation(locationWithWeather.location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}