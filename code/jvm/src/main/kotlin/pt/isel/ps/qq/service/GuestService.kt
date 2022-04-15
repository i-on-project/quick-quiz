package pt.isel.ps.qq.service

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.User
import pt.isel.ps.qq.data.UserDto
import pt.isel.ps.qq.data.UserTokenDto
import pt.isel.ps.qq.database.InMemoryDatabase
import java.util.UUID

@Component
class GuestService(
    private val database: InMemoryDatabase
) {

    fun registerUser(dto: UserDto): UserTokenDto {
        val uid = UUID.randomUUID()
        if(dto.displayName == null) throw java.lang.IllegalStateException("This should NEVER happen")
        val user = User(userName = dto.userName, displayName = dto.displayName, id = uid.toString())
        database.createUser(user)
        return UserTokenDto(token = uid.toString(), dto)
    }

}