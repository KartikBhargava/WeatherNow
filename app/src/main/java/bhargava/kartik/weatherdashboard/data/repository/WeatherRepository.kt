package bhargava.kartik.weatherdashboard.data.repository

import bhargava.kartik.weatherdashboard.data.remote.api.WeatherApiService
import bhargava.kartik.weatherdashboard.data.remote.dto.toWeather
import bhargava.kartik.weatherdashboard.data.remote.dto.toWeatherForecast
import bhargava.kartik.weatherdashboard.domain.model.Weather
import bhargava.kartik.weatherdashboard.domain.model.WeatherForecast
import bhargava.kartik.weatherdashboard.utils.ErrorHandler
import bhargava.kartik.weathernow.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {

    suspend fun getCurrentWeather(cityName: String): Result<Weather> {
        return try {
            val response = apiService.getCurrentWeather(
                cityName = cityName,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.success(response.toWeather())
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<Weather> {
        return try {
            val response = apiService.getCurrentWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.success(response.toWeather())
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getForecast(cityName: String): Result<WeatherForecast> {
        return try {
            val response = apiService.getForecast(
                cityName = cityName,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.success(response.toWeatherForecast())
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getForecastByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherForecast> {
        return try {
            val response = apiService.getForecastByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.success(response.toWeatherForecast())
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
}