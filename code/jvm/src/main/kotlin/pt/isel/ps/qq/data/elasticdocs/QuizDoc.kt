package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

enum class QuestionType {
    MULTIPLE_CHOICE, SHORT, LONG
}

@Document(indexName = "quizzes")
data class QuizDoc(
    @Id val id: String,
    val sessionId: String,
    val userOwner: String, // parametro de pesquisa para templates por user
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>? = null,
    val quizState: QqStatus,
    val numberOfAnswers: Int
)

data class MultipleChoice(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)