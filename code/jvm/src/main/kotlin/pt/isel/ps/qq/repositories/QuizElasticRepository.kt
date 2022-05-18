package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.SessionQuizDoc
import org.springframework.stereotype.Repository

@Repository
interface QuizElasticRepository: ElasticsearchRepository<SessionQuizDoc, String> {
    fun findQuizDocsBySessionId(sessionId: String): List<SessionQuizDoc>
}