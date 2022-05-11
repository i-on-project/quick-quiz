package pt.isel.ps.qq.data

import pt.isel.ps.qq.data.elasticdocs.*

data class SessionSummaryOutputModel(
    val name: String,
    val status: QqStatus,
    val description: String?
) {
    constructor(session: SessionDoc): this(
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
    val limitOfParticipants: Int
) {
    constructor(session: SessionDoc): this(
        name = session.name,
        status = session.status,
        description = session.description,
        creationDate = session.creationDate,
        guestCode = session.guestCode,
        limitOfParticipants = session.limitOfParticipants
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
    val total: Int
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
    constructor(template: TemplateDoc): this(
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
    constructor(input: QuizTemplate): this(
        question = input.question,
        answerType = input.answerType,
        answerChoices = input.answerChoices?.map {
            MultipleChoiceOutputModel(it.choiceNumber, it.choiceAnswer, it.choiceRight)
        }
    )
}

data class MultipleChoiceOutputModel(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)
