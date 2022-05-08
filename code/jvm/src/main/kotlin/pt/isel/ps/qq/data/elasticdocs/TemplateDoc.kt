package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import pt.isel.ps.qq.exceptions.AtLeast1CorrectChoice
import pt.isel.ps.qq.exceptions.AtLeast2Choices

@Document(indexName = "templates")
data class TemplateDoc(
    @Id val id: String,
    val owner: String,
    val limitOfParticipants: Int? = 10,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val quizzes: List<QuizTemplate> = emptyList()
)

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
}