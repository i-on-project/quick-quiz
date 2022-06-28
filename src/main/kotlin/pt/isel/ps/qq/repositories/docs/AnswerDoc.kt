package pt.isel.ps.qq.repositories.docs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection  ="guest_sessions")
data class AnswersDoc(
    @Id
    val id: String,
    val sessionId: String,
    val answers: MutableList<Answer> = mutableListOf()
)

data class Answer(
    val quizId: String,
    val answer: String? = null,
    val answerNumber: Int? = null
)

