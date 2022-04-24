package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.JoinSessionInputModel
import pt.isel.ps.qq.data.elasticdocs.Answer
import pt.isel.ps.qq.repositories.GuestSessionElasticRepository
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.data.elasticdocs.GuestSessionDoc
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

}