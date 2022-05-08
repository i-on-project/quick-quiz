package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document


@Document(indexName = "sessions")
data class SessionDoc(
    @Id
    val id: String,
    val name: String,
    val owner: String,
    val guestCode: Int,
    val limitOfParticipants: Int = 0,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val endDate: Long? = null,
    val quizzes: List<Quiz>? = null,
    val status: QqStatus,
    var numberOfParticipants: Int = 0

) {

    constructor(session: SessionDoc, status: QqStatus): this(
        id = session.id,
        name = session.name,
        owner = session.owner,
        guestCode = session.guestCode,
        limitOfParticipants = session.limitOfParticipants,
        geolocation = session.geolocation,
        radius = session.radius,
        radiusUnit = session.radiusUnit,
        endDate = session.endDate,
        quizzes = session.quizzes,
        status = status,
        numberOfParticipants = session.numberOfParticipants
    )
}

enum class QqStatus {
    NOT_STARTED, STARTED, CLOSED
}

data class Quiz(
    val id: String,
    val order: Int,
    val question: String,
    val answerType: String,
    val answerChoices: List<AChoice>? = null,
    val quizState: QqStatus,
    val status: QqStatus,
    val numberOfAnswers: Int
)

data class AChoice(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)

