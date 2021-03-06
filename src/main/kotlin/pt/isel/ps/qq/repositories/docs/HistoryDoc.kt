package pt.isel.ps.qq.repositories.docs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pt.isel.ps.qq.utils.getCurrentTimeSeconds

@Document(collection  = "history")
data class HistoryDoc(
    @Id val id: String,
    val name: String,
    val description: String? = null,
    val historyDate: Long = getCurrentTimeSeconds(),
    val owner: String,
    val limitOfParticipants: Int,
    val liveDate: Long,
    val quizzes: List<HistoryQuiz>,
    var numberOfParticipants: Int
) {
    constructor(session: SessionDoc, quizzes: List<SessionQuizDoc>, participants: List<ParticipantDoc>): this(
        id = session.id,
        name = session.name,
        description = session.description,
        owner = session.owner,
        limitOfParticipants = session.limitOfParticipants,
        liveDate = session.liveDate!!,
        quizzes = quizzes.map { quiz ->
            val aux = mutableListOf<HistoryAnswer>()
            participants.forEach { answers ->
                val ans = answers.answers.find { quiz.id == it.quizId }
                if(ans != null) aux.add(HistoryAnswer(answers.id, ans.answer, ans.answerNumber))
            }
            HistoryQuiz(
                question = quiz.question,
                order = quiz.order,
                answerType = quiz.answerType,
                answerChoices = quiz.answerChoices ?: emptyList(),
                numberOfAnswers = aux.size,
                answers = aux
            )
        },
        numberOfParticipants = participants.size
    )
}

data class HistoryAnswer(
    val participantId: String,
    val answer: String? = null,
    val choiceNumber: Int? = null
)

data class HistoryQuiz(
    val question: String,
    val order: Int, // posição da questão numa sessão
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>,
    val numberOfAnswers: Int,
    val answers: List<HistoryAnswer>
)
