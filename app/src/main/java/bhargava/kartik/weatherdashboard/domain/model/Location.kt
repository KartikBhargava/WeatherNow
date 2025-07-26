package bhargava.kartik.weatherdashboard.domain.model

data class Location(
    val id: Int = 0,
    val name: String,
    val country: String = "",
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val isFavorite: Boolean = false
)
