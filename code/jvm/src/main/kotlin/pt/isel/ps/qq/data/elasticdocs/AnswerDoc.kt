package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName ="guest_sessions")
data class AnswersDoc(
    @Id
    val id: String,
    val sessionId: String,
    val answers: List<Answer> = emptyList()
)

data class Answer(
    val quizId: String,
    val answer: String? = null,
    val answerNumber: Int? = null
)