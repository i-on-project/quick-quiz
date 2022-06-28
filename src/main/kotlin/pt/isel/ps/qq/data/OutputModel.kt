package pt.isel.ps.qq.data

import pt.isel.ps.qq.data.docs.*

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
    val guestCode: Int?,
    val limitOfParticipants: Int,
    val geolocation: String?,
    val radius: Int?,
    val quizzes: List<String>,
) {
    constructor(session: SessionDoc) : this(
        name = session.name,
        status = session.status,
        description = session.description,
        creationDate = session.creationDate,
        guestCode = session.guestCode,
        limitOfParticipants = session.limitOfParticipants,
        geolocation = session.geolocation,
        radius = session.radius,
        quizzes = session.quizzes
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
    val limitOfParticipants: Int,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val quizzes: List<QuizTemplateOutputModel>
) {
    constructor(template: TemplateDoc) : this(
        limitOfParticipants = template.limitOfParticipants,
        geolocation = template.geolocation,
        radius = template.radius,
        radiusUnit = template.radiusUnit,
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
    constructor(doc: AnswersDoc) : this(
        id = doc.id,
        answers = doc.answers.map { a -> Answer(a.quizId, a.answer, a.answerNumber) }
    )
}

data class Answer(
    val quizId: String,
    val answer: String? = null,
    val answerNumber: Int? = null
)

data class ParticipantOutputModel(
    val participantId: String
)