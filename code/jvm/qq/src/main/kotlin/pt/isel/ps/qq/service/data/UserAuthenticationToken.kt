package pt.isel.ps.qq.service.data

data class UserAuthenticationToken(
    val token: String
) {
    fun getUserId(): String {
        return ""
    }

    fun getUserPassword() {

    }
}
