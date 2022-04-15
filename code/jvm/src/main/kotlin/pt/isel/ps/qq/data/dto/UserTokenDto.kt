package pt.isel.ps.qq.data.dto

import pt.isel.ps.qq.data.dto.UserDto

data class UserTokenDto(
    val token: String,
    val user: UserDto,
    val expireDate: Long? = null
)