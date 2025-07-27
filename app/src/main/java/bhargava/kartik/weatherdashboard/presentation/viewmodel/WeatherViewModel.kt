package bhargava.kartik.weatherdashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bhargava.kartik.weatherdashboard.data.repository.WeatherRepository
import bhargava.kartik.weatherdashboard.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI State - This is what Compose will observe
data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // Private mutable state
    private val _uiState = MutableStateFlow(WeatherUiState())

    // Public read-only state for Compose
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        // Load default weather on startup
        loadWeather("London")
    }

    fun loadWeather(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = repository.getCurrentWeather(cityName)
                result.onSuccess { weather ->
                    _uiState.value = _uiState.value.copy(
                        weather = weather,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "An error occurred while loading weather data"
                )
            }
        }
    }

    fun loadWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = repository.getCurrentWeatherByCoordinates(latitude, longitude)
                result.onSuccess { weather ->
                    _uiState.value = _uiState.value.copy(
                        weather = weather,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "An error occurred while loading weather data"
                )
            }
        }
    }

    fun refreshWeather() {
        val currentWeather = _uiState.value.weather
        if (currentWeather != null) {
            loadWeather(currentWeather.locationName)
        } else {
            loadWeather("London") // Default fallback
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}