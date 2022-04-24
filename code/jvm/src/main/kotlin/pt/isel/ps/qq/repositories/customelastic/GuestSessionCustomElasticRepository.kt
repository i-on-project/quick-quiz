package pt.isel.ps.qq.repositories.customelastic

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.index.query.MatchQueryBuilder
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
        val map = mapOf("answer" to mapOf("quizId" to input.quizId, "answer" to input.answer))
        val response = elasticCustom.buildAndSendRequest(
            "guest_sessions",
            MatchQueryBuilder("id", input.guestId),
            map,
            script
        )

        if (response.noops > 0) throw NumberOfParticipantsExceeded()
        if (response.updated == 0L) throw Exception("There was no update in guest_session") else {
        }
    } catch (e: Exception) {
        println(e)
    }
}