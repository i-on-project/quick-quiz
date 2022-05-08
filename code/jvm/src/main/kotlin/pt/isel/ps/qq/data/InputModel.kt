package pt.isel.ps.qq.data

import pt.isel.ps.qq.data.elasticdocs.QuestionType
import java.util.UUID

private val emailRegex = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")

data class SessionInputModel(
    val name: String,
    val limitOfParticipants: Int,
    val geolocation: String?,
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

data class GiveAnswerInputModel(
    val guestId: String,
    val quizId: String,
    val answer: String
)

data class EditSessionInputModel(
    val name: String?,
    val limitOfParticipants: Int?,
    val geolocation: String?,
    val description: String?
)

data class MultipleChoiceInputModel(
    val choiceNumber: Int? = 0,
    val choice: String,
    val choiceRight: Boolean
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
    val addChoices: List<MultipleChoiceInputModel>?,
    val removeChoices: List<String>?
)
