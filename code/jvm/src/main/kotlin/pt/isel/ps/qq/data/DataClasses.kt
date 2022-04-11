package pt.isel.ps.qq.data

data class JoinSession(
    val guestId: String,
    val sessionId: String,
    val location: String?
)

data class GiveAnswer(
    val sessionId: String,
    val quizId: String,
    val guestId: String,
    val questionType: String,
    val questionNumber: Int,
    val answerNumber: Int?,
    val answerText: String?
)




