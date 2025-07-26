package bhargava.kartik.weatherdashboard.utils

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {

    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    401 -> "Invalid API key. Please check your configuration."
                    404 -> "Location not found. Please check the city name."
                    429 -> "API limit reached. Please try again later."
                    500, 502, 503, 504 -> "Server error. Please try again later."
                    else -> "Network error. Please try again."
                }
            }
            is UnknownHostException -> "No internet connection. Please check your network."
            is ConnectException -> "Connection failed. Please check your internet connection."
            is SocketTimeoutException -> "Request timeout. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }
}