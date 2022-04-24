package pt.isel.ps.qq.repositories.customelastic

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.index.query.TermQueryBuilder
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.elasticdocs.Answer
import pt.isel.ps.qq.exceptions.NumberOfParticipantsExceeded

interface GuestSessionCustomElasticRepository {
    fun updateAnswerList(input: GiveAnswerInputModel)
}

class GuestSessionCustomElasticRepositoryImpl(
    private val jsonMapper: ObjectMapper,
    private val elasticCustom: CustomElasticRequests
) : GuestSessionCustomElasticRepository {

    override fun updateAnswerList(input: GiveAnswerInputModel) = try {
        val script =
            "ctx._source.answers.add(params.answer)"
        val response = elasticCustom.buildAndSendRequest(
            "guest_sessions",
            TermQueryBuilder("id", input.guestId),
            mapOf("answer" to jsonMapper.writeValueAsString(Answer(quizId = input.quizId, answer = input.answer))),
            script
        )

        if (response.noops > 0) throw NumberOfParticipantsExceeded()
        if (response.updated == 0L) throw Exception("There was no update in guest_sseion") else {
        }
    } catch (e: Exception) {
        println(e)
    }
}