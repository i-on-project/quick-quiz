package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.JoinSessionInputModel
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.data.elasticdocs.Answer
import pt.isel.ps.qq.repositories.GuestSessionElasticRepository
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.data.elasticdocs.GuestSessionDoc
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionDoc
import pt.isel.ps.qq.exceptions.OpenedSessionException
import java.util.*

@Service
class SessionService(
    private val sessionRepo: SessionElasticRepository,
    private val guestSessionRepo: GuestSessionElasticRepository
) {
    fun joinSession(input: JoinSessionInputModel): GuestSessionDoc {
        sessionRepo.updateNumberOfParticipants(input.sessionCode)
        val session = sessionRepo.findSessionDocByGuestCode(input.sessionCode) ?: throw Exception("There was no session with that guest code")
        val guestUuid = UUID.randomUUID().toString()
        val guestSession = GuestSessionDoc(id=guestUuid, sessionId = session.id)
        guestSessionRepo.save(guestSession)
        return guestSession
    }

    fun giveAnswer(input: GiveAnswerInputModel): GuestSessionDoc {
        guestSessionRepo.updateAnswerList(input)
        val opt =  guestSessionRepo.findById(input.guestId)
        if(opt.isEmpty) throw Exception("Invalid guest code... this guest may not be in the session" )
        return opt.get()
    }

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

    fun deleteSession(user: String, id: String) {
        val opt = sessionRepo.findById(id)
        if(opt.isEmpty) throw Exception("Not found")
        val doc = opt.get()
        if(doc.owner != user) throw IllegalStateException("The user $user don´t have authority over this session") // maybe 403
        sessionRepo.deleteById(id)
    }

    fun getAllSessions(user: String, id: String): List<SessionDoc> {
        return sessionRepo.findSessionDocsByOwner(user)
    }

}