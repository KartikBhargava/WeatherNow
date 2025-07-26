// Create: presentation/viewmodel/ForecastViewModel.kt
package bhargava.kartik.weatherdashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bhargava.kartik.weatherdashboard.data.repository.WeatherRepository
import bhargava.kartik.weatherdashboard.domain.model.WeatherForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForecastUiState(
    val forecast: WeatherForecast? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    init {
        // Load default forecast on startup
        loadForecast("London")
    }

    fun loadForecast(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = repository.getForecast(cityName)
                result.onSuccess { forecast ->
                    _uiState.value = _uiState.value.copy(
                        forecast = forecast,
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
                    errorMessage = "An error occurred while loading forecast data"
                )
            }
        }
    }

    fun loadForecastByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = repository.getForecastByCoordinates(latitude, longitude)
                result.onSuccess { forecast ->
                    _uiState.value = _uiState.value.copy(
                        forecast = forecast,
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
                    errorMessage = "An error occurred while loading forecast data"
                )
            }
        }
    }

    fun refreshForecast() {
        val currentForecast = _uiState.value.forecast
        if (currentForecast != null) {
            loadForecast(currentForecast.cityName)
        } else {
            loadForecast("London") // Default fallback
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}