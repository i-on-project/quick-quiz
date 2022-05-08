package pt.isel.ps.qq.repositories.customelastic

import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.elasticdocs.Answer

interface AnswerCustomRequests {
    fun updateAnswerList(input: GiveAnswerInputModel)
}

class AnswerCustomRequestsImpl(private val request: ElasticRequest) : AnswerCustomRequests {

    override fun updateAnswerList(input: GiveAnswerInputModel) {
        val script = "ctx._source.answers.add(params.answer)"
        val response = request.updateDocument(
            "guest_sessions",
            input.guestId,
            mapOf("answer" to Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice)),
            script
        )
        if(response.status().status != 200) throw Exception("Document was not updated")
    }

}