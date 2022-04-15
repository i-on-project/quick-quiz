package pt.isel.ps.qq.data

data class UserTokenDto(
    val token: String,
    val user: UserDto,
    val expireDate: Long? = null
)