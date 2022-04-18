package pt.isel.ps.qq.database

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.Session
import pt.isel.ps.qq.data.User
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.exceptions.UserNotFoundException

@Component
class InMemoryDatabase: IDatabase {

    private val userTable: HashMap<String, User> = HashMap()
    private val sessionTable: HashMap<String, Session> = HashMap()

    fun createUser(user: User) {
        if(userTable[user.userName] != null) throw AlreadyExistsException()
        userTable[user.userName] = user
    }

    fun getUser(username: String): User = userTable[username] ?: throw UserNotFoundException()

    fun updateUser(user: User) {
        userTable[user.userName] ?: throw UserNotFoundException()
        userTable[user.userName] = user
    }

    fun createSession(session: Session) {
        if(sessionTable[session.sessionId] != null) throw AlreadyExistsException()
        sessionTable[session.sessionId] = session
    }

    fun query(guestCode: Int) {
        sessionTable.forEach { if(it.value.guestCode == guestCode && it.value.state.todo != "closed") throw AlreadyExistsException() }
    }

}