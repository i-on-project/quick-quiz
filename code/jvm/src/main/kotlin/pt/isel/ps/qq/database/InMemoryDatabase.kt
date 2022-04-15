package pt.isel.ps.qq.database

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.User
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.exceptions.UserNotFoundException

@Component
class InMemoryDatabase {

    private val userTable: HashMap<String, User> = HashMap()

    fun createUser(user: User) {
        if(userTable[user.userName] != null) throw AlreadyExistsException()
        userTable[user.userName] = user
    }

    fun getUser(username: String): User = userTable[username] ?: throw UserNotFoundException()

    fun updateUser(user: User) {
        userTable[user.userName] ?: throw UserNotFoundException()
        userTable[user.userName] = user
    }

}