package pt.isel.ps.qq.repositories.docs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pt.isel.ps.qq.data.CreateTemplateInputModel
import pt.isel.ps.qq.data.QuizTemplateInputModel
import pt.isel.ps.qq.exceptions.AtLeast1CorrectChoice
import pt.isel.ps.qq.exceptions.AtLeast2Choices
import java.util.*

@Document(collection  = "templates")
data class TemplateDoc(
    @Id val id: String,
    val name: String,
    val owner: String,
    val limitOfParticipants: Int = 10,
    val geolocation: String? = null,
    val radius: Double? = null,
    val quizzes: List<QuizTemplate> = emptyList()
) {
    constructor(owner: String, input: CreateTemplateInputModel): this(
        id = UUID.randomUUID().toString(),
        name = input.name,
        owner = owner,
        limitOfParticipants = input.limitOfParticipants ?: 10,
        geolocation = input.geolocation,
        radius = input.radius,
        quizzes = input.quizzes.map { QuizTemplate(it) }
    )
}

data class QuizTemplate(
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>? = null,
) {
    init {
        if(answerType == QuestionType.MULTIPLE_CHOICE) {
            if(answerChoices == null || answerChoices.count() < 2) throw AtLeast2Choices()
            answerChoices.find { it.choiceRight } ?: throw AtLeast1CorrectChoice()
        }
    }

    constructor(input: QuizTemplateInputModel): this(
        order = input.order, // posição da questão numa sessão
        question = input.question,
        answerType = input.answerType,
        answerChoices = input.answerChoices?.map {
            MultipleChoice(it.choiceNumber ?: 0, it.choiceAnswer, it.choiceRight)
        },
    )
}