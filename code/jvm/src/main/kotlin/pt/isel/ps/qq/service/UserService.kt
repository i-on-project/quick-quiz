package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.exceptions.OpenedSessionException
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionDoc
import java.util.*


@Service
class UserService(private val sessionRepo: SessionElasticRepository) {

    fun createSession(owner: String, input: SessionInputModel): SessionDoc {

        val openSessions = sessionRepo.findSessionDocsByOwnerAndStatus(owner, QqStatus.STARTED)
        if (openSessions.isNotEmpty()) throw OpenedSessionException()

        val sessionId = UUID.randomUUID().toString()
        var guestCode = sessionId.hashCode()
        var count = 0
        while (!validateUniqueGuestCode(guestCode)) {
            if(count >= 3) throw Exception("We dont know what to do here")
            guestCode *= 31
            ++count
        }
        guestCode = if (guestCode < 0) guestCode * -1 else guestCode

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            owner = owner,
            guestCode = guestCode,
            limitOfParticipants = input.limitOfParticipants,
            endDate = input.endDate,
            status = QqStatus.NOT_STARTED,
            numberOfParticipants = 0
        )
        return sessionRepo.save(session)

    }

    private fun validateUniqueGuestCode(code: Int): Boolean {
        val doc = sessionRepo.findSessionDocByGuestCodeAndStatusNot(code, QqStatus.CLOSED)
        return doc == null
    }
}