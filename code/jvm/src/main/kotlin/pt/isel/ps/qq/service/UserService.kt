package pt.isel.ps.qq.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.dto.input.SessionInputModel
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.repositories.elasticdocs.QqStatus
import pt.isel.ps.qq.repositories.elasticdocs.SessionDoc
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.exceptions.OpenedSessionException
import java.util.*


@Component
class UserService() {

/*
    @Autowired
    private val userRepo: UserElasticRepository? = null
*/

    @Autowired
    private val sessionRepo: SessionElasticRepository? = null

    fun createSession(input: SessionInputModel): SessionDoc {

        val openSessions = sessionRepo?.findSessionDocsByOwnerAndStatus(input.owner, QqStatus.STARTED)
        if (openSessions!!.isNotEmpty()) throw OpenedSessionException()

        val sessionId = UUID.randomUUID().toString()
        var guestCode = sessionId.hashCode()
        while (!validateUniqueGuestCode(guestCode)) guestCode *= 31
        guestCode = if (guestCode < 0) guestCode * -1 else guestCode

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            owner = input.owner,
            guestCode = guestCode,
            limitOfParticipants = input.limitOfParticipants,
            endDate = input.endDate,
            status = QqStatus.NOT_STARTED,
            numberOfParticipants = 0
        )
        return sessionRepo?.save(session) as SessionDoc

    }

    private fun validateUniqueGuestCode(code: Int): Boolean {
        return try {
            //database.query(code)
            true
        } catch (e: AlreadyExistsException) {
            false
        }
    }
}