package pt.isel.ps.qq.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.docs.ParticipantDoc


@Repository
interface ParticipantRepository: MongoRepository<ParticipantDoc, String> {
    fun findAnswersDocsBySessionId(sessionId: String): List<ParticipantDoc>
}