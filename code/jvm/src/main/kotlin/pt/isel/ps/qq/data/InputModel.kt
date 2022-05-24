package pt.isel.ps.qq.data

import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.QuestionType
import java.util.UUID

private val emailRegex = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")

data class SessionInputModel(
    val name: String,
    val description: String? = null,
    val limitOfParticipants: Int?,
    val geolocation: String?,
    val radius: Int?,
    val templateId: String? = null
)

data class RegisterInputModel(
    val userName: String,
    val displayName: String
){
    init {
        require(userName.matches(emailRegex))
        require(displayName.isNotBlank())
    }
}

data class LoginInputModel(
    val userName: String
) {
    init {
        require(userName.matches(emailRegex)) { "Invalid Email " }
    }
}

data class LoginMeInputModel(
    val userName: String,
    val loginToken: String
) {
    init {
        require(userName.matches(emailRegex)) { "Invalid Email " }
        require(UUID.fromString(loginToken) != null) {"Invalid Token"}
    }
}

data class JoinSessionInputModel(
    val sessionCode: Int
)

data class GetQuizInputModel(
    val guestId: String,
    val sessionId: String,
    val quizId: String
)

data class GiveAnswerInputModel(
    val guestId: String,
    val sessionId: String,
    val quizId: String,
    val answer: String? = null,
    val answerChoice: Int? = null
)

data class EditSessionInputModel(
    val name: String?,
    val limitOfParticipants: Int?,
    val geolocation: String?,
    val radius: Int?,
    val description: String?,
    val status: QqStatus?
    //TODO: Missing radius
)

data class MultipleChoiceInputModel(
    val choiceNumber: Int? = 0,
    val choiceAnswer: String,
    val choiceRight: Boolean = false
)

data class AddQuizToSessionInputModel(
    val order: Int?,
    val question: String,
    val questionType: QuestionType,
    val choices: List<MultipleChoiceInputModel>?
)

data class EditQuizInputModel(
    val order: Int?,
    val question: String?,
    val choices: List<MultipleChoiceInputModel>?,
    //val removeChoices: List<String>?
)

data class UpdateQuizStausInputModel(
    val quizState: QqStatus
)

data class CreateTemplateInputModel(
    val limitOfParticipants: Int?,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val quizzes: List<QuizTemplateInputModel> = emptyList()
)

data class QuizTemplateInputModel(
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoiceInputModel>? = null,
)