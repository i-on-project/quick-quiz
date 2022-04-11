package pt.isel.ps.qq.data

data class JoinSession(
    val guestId: String,
    val sessionId: String,
    val location: String? = null
)

data class GiveAnswer(
    val sessionId: String,
    val quizId: String,
    val guestId: String,
    val questionType: String,
    val questionNumber: Int,
    val answerNumber: Int? = null,
    val answerText: String? = null
)


data class User(
    val userName: String,
    val emailAddress: String
)