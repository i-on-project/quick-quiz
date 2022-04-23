package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.dto.input.JoinSessionInputModel
import pt.isel.ps.qq.repositories.GuestSessionElasticRepository
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.repositories.elasticdocs.GuestSessionDoc
import java.util.*

@Service
class SessionService(private val sessionRepo: SessionElasticRepository,
                     private val guestSessionRepo: GuestSessionElasticRepository
) {
    fun joinSession(input: JoinSessionInputModel): GuestSessionDoc {
        sessionRepo.updateNumberOfParticipants(input.sessionCode)
        val session = sessionRepo.findSessionDocByGuestCode(input.sessionCode)
        val guestUuid = UUID.randomUUID().toString()
        val guestSession = GuestSessionDoc(id=guestUuid, sessionId = session.id)
        guestSessionRepo.save(guestSession)
        return guestSession
    }

}