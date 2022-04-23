package pt.isel.ps.qq.data.dto.input

import java.util.UUID

private val emailRegex = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")

/*data class GeolocationInputModel(
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val radiusUnit: String
)*/

data class SessionInputModel(
    val name: String,
    val owner: String,
    val limitOfParticipants: Int,
    val geolocation: String?,
    val endDate: Long?, //seconds
    val templateId: String? = null
)

data class RegisterInputModel(
    val userName: String,
    val displayName: String
){
    init {
        require(userName.matches(emailRegex))
        require(displayName.isNotBlank())
    }
}

data class LoginInputModel(
    val userName: String
) {
    init {
        require(userName.matches(emailRegex)) { "Invalid Email " }
    }
}

data class LoginMeInputModel(
    val userName: String,
    val loginToken: String
) {
    init {
        require(userName.matches(emailRegex)) { "Invalid Email " }
        require(UUID.fromString(loginToken) != null) {"Invalid Token"}
    }
}

data class JoinSessionInputModel(
    val sessionCode: Int
)