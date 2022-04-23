package pt.isel.ps.qq.repositories.elasticdocs

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

)

/*data class SessionState(
    val status: QqStatus,
    val numberOfParticipants: Int
)*/

enum class QqStatus {
    NOT_STARTED, STARTED, CLOSED
}

/*
data class QuizState(
    val status: QqStatus,
    val numberOfAnswers: Int
)
*/

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

/*
data class SessionSettings(
    val limitOfParticipants: Int,
    val geolocation: Geolocation? = null,
    val endDate: Long? = null
)

data class Geolocation(
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val radiusUnit: String
)*/
