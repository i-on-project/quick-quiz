package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionQuizDoc

@Repository
interface QuizElasticRepository: ElasticsearchRepository<SessionQuizDoc, String> {
    fun findQuizDocsBySessionId(sessionId: String): List<SessionQuizDoc>
    fun findSessionQuizDocsBySessionIdAndQuizStateNot(sessionId: String, qqStatus: QqStatus): List<SessionQuizDoc>
}