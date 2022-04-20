package pt.isel.ps.qq.data.dto

import pt.isel.ps.qq.data.User
import pt.isel.ps.qq.data.dto.UserDto

data class UserTokenDto(
    val token: String,
    val user: UserDto,
    val expireDate: Long? = null
) {
    constructor(token: String, user: User): this(token = token, user = UserDto(userName = user.userName, displayName = user.displayName), expireDate=user.idExpiredDate)

}