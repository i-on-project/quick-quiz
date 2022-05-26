package pt.isel.ps.qq.repositories.customelastic

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.elasticdocs.Answer

interface AnswerCustomRequests {
    fun updateAnswerList(input: GiveAnswerInputModel)
}

class AnswerCustomRequestsImpl(private val request: ElasticRequest) : AnswerCustomRequests {

    override fun updateAnswerList(input: GiveAnswerInputModel) {
        val script = "ctx._source.answers.add(params.answer)"

        val tmp = Json.encodeToString(Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice))
        val response = request.updateDocument(
            "guest_sessions",
            input.guestId,
            mapOf("answer" to  tmp),
            script
        )
        if(response.status().status != 200) throw Exception("Document was not updated")
    }

}