package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import pt.isel.ps.qq.utils.getCurrentTimeSeconds

@Document(indexName = "history")
data class HistoryDoc(
    @Id val id: String,
    val name: String,
    val description: String? = null,
    val historyDate: Long = getCurrentTimeSeconds(),
    val owner: String,
    val limitOfParticipants: Int,
    val liveDate: Long?,
    val quizzes: List<HistoryQuiz>,
    val answers: List<HistoryAnswer>,
    var numberOfParticipants: Int
) {
    constructor(session: SessionDoc, quizzes: List<QuizDoc>): this(
        id = session.id,
        name = session.name,
        description = session.description,
        owner = session.owner,
        limitOfParticipants = session.limitOfParticipants,
        liveDate = session.liveDate,
        quizzes = quizzes.map {
            HistoryQuiz(
                question = it.question,
                order = it.order,
                answerType = it.answerType,
                answerChoices = it.answerChoices ?: emptyList(),
                numberOfAnswers = it.numberOfAnswers
            )
        },
        answers = emptyList(), //TODO: get the real answers
        numberOfParticipants = session.numberOfParticipants
    )
}

data class HistoryAnswer(
    val answer: String?,
    val choiceNumber: Int?
)

data class HistoryQuiz(
    val question: String,
    val order: Int, // posição da questão numa sessão
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice> = emptyList(),
    val numberOfAnswers: Int
)
