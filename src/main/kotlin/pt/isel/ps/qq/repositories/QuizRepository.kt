package pt.isel.ps.qq.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.SessionQuizDoc

@Repository
interface QuizRepository: MongoRepository<SessionQuizDoc, String> {
    fun findQuizDocsBySessionId(sessionId: String): List<SessionQuizDoc>
    fun findSessionQuizDocsBySessionIdAndQuizStatusNot(sessionId: String, qqStatus: QqStatus): List<SessionQuizDoc>
}