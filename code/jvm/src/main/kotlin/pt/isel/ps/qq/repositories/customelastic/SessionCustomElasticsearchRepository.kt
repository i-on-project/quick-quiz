package pt.isel.ps.qq.repositories.customelastic


import org.elasticsearch.index.query.MatchPhraseQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.TermQueryBuilder
import org.elasticsearch.index.reindex.BulkByScrollResponse
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.exceptions.NumberOfParticipantsExceeded
import pt.isel.ps.qq.utils.getCurrentTimeSeconds


interface SessionCustomElasticsearchRepository {
    fun updateNumberOfParticipants(sessionCode: Int)
    fun updateStatus(id: String, newStatus: QqStatus)
    fun updateStatusAndDate(id: String, newStatus: QqStatus)
    fun updateQuizzes(id: String, quizId: String)
}

class SessionCustomElasticsearchRepositoryImpl(
    private val elasticCustom: CustomElasticRequests
) : SessionCustomElasticsearchRepository {

    override fun updateNumberOfParticipants(sessionCode: Int) {
        val script = "if (ctx._source.numberOfParticipants < ctx._source.limitOfParticipants ) {ctx._source.numberOfParticipants++ ;} else {ctx.op = 'noop';} "
        val query = TermQueryBuilder("guestCode", sessionCode)
        val response = request(query, emptyMap(), script)
        if (response.noops > 0) throw NumberOfParticipantsExceeded()
        if (response.updated == 0L) throw Exception("it was not updated")
    }

    override fun updateStatus(id: String, newStatus: QqStatus) {
        val script = "ctx._source.status = \'$newStatus\'"
        val query = MatchPhraseQueryBuilder("id", id)
        val response = request(query, emptyMap(), script)
        if(response.updated == 0L) throw Exception("It was not updated")
    }

    override fun updateStatusAndDate(id: String, newStatus: QqStatus) {
        val script = "ctx._source.status = \'$newStatus\'; ctx._source.liveDate = ${getCurrentTimeSeconds()}"
        val query = MatchPhraseQueryBuilder("id", id)
        val response = request(query, emptyMap(), script)
        if(response.updated == 0L) throw Exception("It was not updated")
    }

    override fun updateQuizzes(id: String, quizId: String) {
        val script = "ctx._source.quizzes.add(params.quizId)"
        val query = MatchPhraseQueryBuilder("id", id)
        val params = mapOf("quizId" to quizId)
        val response = request(query, params, script)
        if(response.updated == 0L) throw Exception("It was not updated")
    }

    private fun request(query: QueryBuilder, params: Map<String, Any>, script: String): BulkByScrollResponse {
        return elasticCustom.buildAndSendRequest("sessions", query, params, script)
    }
}