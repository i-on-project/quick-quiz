package pt.isel.ps.qq.data.dto.input

data class GeolocationInputModel(
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val radiusUnit: String
)

data class SessionInputModel(
    val name: String,
    val limitOfParticipants: Int,
    val useGeolocation: Boolean,
    val geolocation: GeolocationInputModel?,
    val endDate: Long, //seconds
    val templateId: String? = null
)
