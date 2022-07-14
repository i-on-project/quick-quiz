package pt.isel.ps.qq.repositories.docs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pt.isel.ps.qq.data.EditQuizInputModel
import pt.isel.ps.qq.data.MultipleChoiceInputModel
import pt.isel.ps.qq.exceptions.AtLeast1CorrectChoice
import pt.isel.ps.qq.exceptions.AtLeast2Choices
import java.util.*

enum class QuestionType {
    MULTIPLE_CHOICE, SHORT, LONG
}

@Document(collection  = "session_quizzes")
data class SessionQuizDoc(
    @Id val id: String,
    val sessionId: String,
    val userOwner: String, // parametro de pesquisa para templates por user
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>? = null,
    val quizStatus: QqStatus, //launched or not
 //   val numberOfAnswers: Int
) {

    init {

        if (answerType == QuestionType.MULTIPLE_CHOICE) {
            if (answerChoices == null || answerChoices.count() < 2) throw AtLeast2Choices()
            answerChoices.find { it.choiceRight } ?: throw AtLeast1CorrectChoice()
        }
    }

    companion object {
        private fun changeChoices(
            doc: SessionQuizDoc,
            add: List<MultipleChoiceInputModel>?,
            rem: List<String>?
        ): List<MultipleChoice>? {
            if (doc.answerType != QuestionType.MULTIPLE_CHOICE) return null
            val toReturn = doc.answerChoices!!.toMutableList()
            if (rem != null && rem.isNotEmpty()) {
                rem.forEach { str ->
                    val toDelete = toReturn.find { it.choiceAnswer == str }
                    if (toDelete != null) toReturn.remove(toDelete)
                }
            }
            if (add != null && add.isNotEmpty()) {
                add.forEach {
                    toReturn.add(MultipleChoice(it.choiceNumber ?: 0, it.choiceAnswer, it.choiceRight))
                }
            }
            return toReturn
        }

        private fun getMultipleChoices(input: List<MultipleChoiceInputModel>?): List<MultipleChoice>? {
            when (input) {
                null -> return null
                else -> return input.map { c -> MultipleChoice(c.choiceNumber ?: 999, c.choiceAnswer, c.choiceRight) }
            }
        }


    }

    constructor(doc: SessionQuizDoc, input: EditQuizInputModel) : this(
        id = doc.id,
        sessionId = doc.sessionId,
        userOwner = doc.userOwner, // parametro de pesquisa para templates por user
        order = input.order ?: doc.order, // posição da questão numa sessão
        question = input.question ?: doc.question,
        answerType = doc.answerType,
        answerChoices = getMultipleChoices(input.choices), //changeChoices(doc, input.addChoices, input.removeChoices),
        quizStatus = doc.quizStatus, //launched or not
       // numberOfAnswers = doc.numberOfAnswers
    )

    constructor(doc: SessionQuizDoc, input: QqStatus) : this(
        id = doc.id,
        sessionId = doc.sessionId,
        userOwner = doc.userOwner, // parametro de pesquisa para templates por user
        order = doc.order, // posição da questão numa sessão
        question = doc.question,
        answerType = doc.answerType,
        answerChoices = doc.answerChoices,
        quizStatus = input, //launched or not
        //numberOfAnswers = doc.numberOfAnswers
    )

    constructor(quiz: QuizTemplate, owner: String, session: String) : this(
        id = UUID.randomUUID().toString(),
        sessionId = session,
        userOwner = owner, // parametro de pesquisa para templates por user
        order = quiz.order, // posição da questão numa sessão
        question = quiz.question,
        answerType = quiz.answerType,
        answerChoices = quiz.answerChoices,
        quizStatus = QqStatus.NOT_STARTED, //launched or not
       // numberOfAnswers = 0
    )
}


@Document(collection = "template_quizzes")
data class TemplateQuizDoc(
    @Id val id: String,
    val templateId: String,
    val userOwner: String, // parametro de pesquisa para templates por user
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>? = null,
    val numberOfAnswers: Int
) {

    init {
        if (answerType == QuestionType.MULTIPLE_CHOICE) {
            if (answerChoices == null || answerChoices.count() < 2) throw AtLeast2Choices()
            answerChoices.find { it.choiceRight } ?: throw AtLeast1CorrectChoice()
        }
    }

    constructor(doc: TemplateQuizDoc, input: EditQuizInputModel) : this(
        id = doc.id,
        templateId = doc.templateId,
        userOwner = doc.userOwner, // parametro de pesquisa para templates por user
        order = input.order ?: doc.order, // posição da questão numa sessão
        question = input.question ?: doc.question,
        answerType = doc.answerType,
        // answerChoices = changeChoices(doc, input.addChoices, input.removeChoices),
        numberOfAnswers = doc.numberOfAnswers
    )


}

data class MultipleChoice(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)