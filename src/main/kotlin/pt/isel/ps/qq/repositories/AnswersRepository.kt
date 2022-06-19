package pt.isel.ps.qq.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.docs.AnswersDoc


@Repository
interface AnswersRepository: MongoRepository<AnswersDoc, String> {
    fun findAnswersDocsBySessionId(sessionId: String): List<AnswersDoc>
}