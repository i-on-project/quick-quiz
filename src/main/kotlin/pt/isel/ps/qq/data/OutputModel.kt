package pt.isel.ps.qq.data

import pt.isel.ps.qq.repositories.docs.*

data class SessionSummaryOutputModel(
    val id: String,
    val name: String,
    val status: QqStatus,
    val description: String?
) {
    constructor(session: SessionDoc) : this(
        id = session.id,
        name = session.name,
        status = session.status,
        description = session.description
    )
}

data class SessionOutputModel(
    val name: String,
    val status: QqStatus,
    val description: String?,
    val creationDate: Long,
    val liveDate: Long?,
    val guestCode: Int?,
    val limitOfParticipants: Int,
    val geolocation: String?,
    val radius: Int?,
    val tags: List<String> = emptyList()
) {
    constructor(session: SessionDoc) : this(
        name = session.name,
        status = session.status,
        description = session.description,
        creationDate = session.creationDate,
        liveDate = session.liveDate,
        guestCode = session.guestCode,
        limitOfParticipants = session.limitOfParticipants,
        geolocation = session.geolocation,
        radius = session.radius,
        tags = session.tags
    )
}

data class ParticipantHistoryOutputModel(
    val date: Long,
    val quizzes: List<SessionQuizzes> = emptyList()
) {
    constructor(doc: HistoryDoc, participantId: String) : this(
        date = doc.historyDate,
        quizzes = doc.quizzes.map { q -> SessionQuizzes(q, participantId) }
    )
}

data class SessionQuizzes(
    val question: String,
    val order: Int = 0,
    val answerType: QuestionType,
    val answers: List<Answer>,
    val answerChoices: List<MultipleChoice> = emptyList()
) {
    constructor(quiz: HistoryQuiz, participantId: String) : this(
        question = quiz.question,
        answerType = quiz.answerType,
        answerChoices = quiz.answerChoices,
        answers = listOf(Answer(quiz.answers, participantId))
    )
}

data class RequestLoginOutputModel(
    val userName: String,
    val displayName: String? = null,
    val token: String? = null,
    val timeout: Long? = null
)

data class Acknowledge(
    val acknowledge: Boolean
) {
    companion object {
        val TRUE = Acknowledge(true)
    }
}

data class ListInfo(
    val size: Int,
    val total: Int,
    val pageSize: Int = 10
)

data class LiveSession(
    val guestCode: String
)

data class TemplateOutputModel(
    val id: String,
    val name: String,
    val limitOfParticipants: Int,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val quizzes: List<QuizTemplateOutputModel>
) {
    constructor(template: TemplateDoc) : this(
        id = template.id,
        name = template.name,
        limitOfParticipants = template.limitOfParticipants,
        geolocation = template.geolocation,
        radius = template.radius,
        quizzes = template.quizzes.sortedBy { it.order }.map { QuizTemplateOutputModel(it) }
    )
}

data class QuizTemplateOutputModel(
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoiceOutputModel>? = null,
) {
    constructor(input: QuizTemplate) : this(
        question = input.question,
        answerType = input.answerType,
        answerChoices = input.answerChoices?.map {
            MultipleChoiceOutputModel(it.choiceNumber, it.choiceAnswer, it.choiceRight)
        }
    )
}

data class HistoryOutputModel(
    val name: String,
    val description: String? = null,
    val historyDate: Long,
    val limitOfParticipants: Int,
    val liveDate: Long,
    val quizzes: List<HistoryQuiz>,
    val numberOfParticipants: Int
) {
    constructor(doc: HistoryDoc) : this(
        name = doc.name,
        description = doc.description,
        historyDate = doc.historyDate,
        limitOfParticipants = doc.limitOfParticipants,
        liveDate = doc.liveDate,
        quizzes = doc.quizzes,
        numberOfParticipants = doc.numberOfParticipants
    )
}

data class MultipleChoiceOutputModel(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)

data class AnswersOutputModel(
    val id: String,
    val answers: List<Answer> = emptyList()
) {
    constructor(doc: ParticipantDoc) : this(
        id = doc.id,
        answers = doc.answers.map { a -> Answer(a.quizId, a.answer, a.answerNumber) }
    )
}

data class Answer(
    val quizId: String,
    val answer: String? = null,
    val choiceNumber: Int? = null
) {
    constructor(answers: List<HistoryAnswer>, participantId: String): this(
        quizId = participantId,
        answer = answers.firstOrNull{ elem -> elem.participantId == participantId }?.answer,
        choiceNumber = answers.firstOrNull{ elem -> elem.participantId == participantId }?.choiceNumber
    )
}

data class ParticipantOutputModel(
    val participantId: String
)