package pt.isel.ps.qq.service

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.User
import pt.isel.ps.qq.data.dto.UserDto
import pt.isel.ps.qq.data.dto.UserTokenDto
import pt.isel.ps.qq.database.InMemoryDatabase
import pt.isel.ps.qq.exceptions.InvalidTokenException
import java.util.UUID

@Component
class GuestService(
    private val database: InMemoryDatabase
) {

    companion object {
        const val TOKEN_TIMEOUT: Long = 604800
    }

    fun register(dto: UserDto): UserTokenDto {
        val uid = UUID.randomUUID()
        if(dto.displayName == null) throw java.lang.IllegalStateException("This should NEVER happen")
        val user = User(userName = dto.userName, displayName = dto.displayName, id = uid.toString())
        database.createUser(user)
        return UserTokenDto(token = uid.toString(), dto)
    }

    fun logmein(dto: UserTokenDto): UserTokenDto {
        val user = database.getUser(dto.user.userName)
        val uid = UUID.fromString(dto.token)
        if(uid.compareTo(UUID.fromString(user.id)) != 0) throw InvalidTokenException()
        val otherUid = UUID.randomUUID().toString()
        val time = (System.currentTimeMillis() / 1000) + TOKEN_TIMEOUT
        val token = UserTokenDto(token = otherUid, user = UserDto(
            userName = user.userName, displayName = user.displayName
        ), expireDate = time)
        database.updateUser(User(user = user, uid = otherUid, time))
        return token
    }

}