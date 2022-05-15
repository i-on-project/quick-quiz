package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import pt.isel.ps.qq.data.EditQuizInputModel
import pt.isel.ps.qq.data.MultipleChoiceInputModel
import pt.isel.ps.qq.exceptions.AtLeast1CorrectChoice
import pt.isel.ps.qq.exceptions.AtLeast2Choices
import java.util.*

enum class QuestionType {
    MULTIPLE_CHOICE, SHORT, LONG
}

@Document(indexName = "quizzes")
data class QuizDoc(
    @Id val id: String,
    val sessionId: String,
    val userOwner: String, // parametro de pesquisa para templates por user
    val order: Int, // posição da questão numa sessão
    val question: String,
    val answerType: QuestionType,
    val answerChoices: List<MultipleChoice>? = null,
    val quizState: QqStatus, //launched or not
    val numberOfAnswers: Int
) {

    init {
        if(answerType == QuestionType.MULTIPLE_CHOICE) {
            if(answerChoices == null || answerChoices.count() < 2) throw AtLeast2Choices()
            answerChoices.find { it.choiceRight } ?: throw AtLeast1CorrectChoice()
        }
    }

    companion object {
        private fun changeChoices(doc: QuizDoc, add: List<MultipleChoiceInputModel>?, rem: List<String>?): List<MultipleChoice>? {
            if(doc.answerType != QuestionType.MULTIPLE_CHOICE) return null
            val toReturn = doc.answerChoices!!.toMutableList()
            if(rem != null && rem.isNotEmpty()) {
                rem.forEach { str ->
                    val toDelete = toReturn.find { it.choiceAnswer == str }
                    if(toDelete != null) toReturn.remove(toDelete)
                }
            }
            if(add != null && add.isNotEmpty()) {
                add.forEach {
                    toReturn.add(MultipleChoice(it.choiceNumber ?: 0, it.choice, it.choiceRight))
                }
            }
            return toReturn
        }
    }

    constructor(doc: QuizDoc, input: EditQuizInputModel): this(
        id = doc.id,
        sessionId = doc.sessionId,
        userOwner = doc.userOwner, // parametro de pesquisa para templates por user
        order = input.order ?: doc.order, // posição da questão numa sessão
        question = input.question ?: doc.question,
        answerType = doc.answerType,
        answerChoices = changeChoices(doc, input.addChoices, input.removeChoices),
        quizState = doc.quizState, //launched or not
        numberOfAnswers = doc.numberOfAnswers
    )

    constructor(quiz: QuizTemplate, owner: String, session: String): this(
        id = UUID.randomUUID().toString(),
        sessionId = session,
        userOwner = owner, // parametro de pesquisa para templates por user
        order = quiz.order, // posição da questão numa sessão
        question = quiz.question,
        answerType = quiz.answerType,
        answerChoices = quiz.answerChoices,
        quizState = QqStatus.NOT_STARTED, //launched or not
        numberOfAnswers = 0
    )
}

data class MultipleChoice(
    val choiceNumber: Int,
    val choiceAnswer: String,
    val choiceRight: Boolean
)