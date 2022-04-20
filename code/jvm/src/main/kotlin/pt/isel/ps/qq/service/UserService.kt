package pt.isel.ps.qq.service

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.Session
import pt.isel.ps.qq.data.SessionState
import pt.isel.ps.qq.data.dto.input.SessionInputModel
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import java.util.*


@Component
class UserService() {

    fun createSession(username: String, input: SessionInputModel) {
        val sessionId = UUID.randomUUID().toString()
        var guestCode = sessionId.hashCode()
        while(!validateGuestCode(guestCode)) guestCode *= 31
        //TODO find the template
        //val session = Session(input = input, sessionId = sessionId, sessionOwner = username, guestCode = guestCode, templates = emptyList(), state = SessionState.getInitialState())
        //database.createSession(session)
    }

    private fun validateGuestCode(code: Int): Boolean {
        return try {
            //database.query(code)
            true
        } catch(e: AlreadyExistsException) {
            false
        }
    }
}