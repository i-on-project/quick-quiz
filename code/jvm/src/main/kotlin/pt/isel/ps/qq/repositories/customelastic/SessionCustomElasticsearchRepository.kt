package pt.isel.ps.qq.repositories.customelastic


import org.elasticsearch.index.query.MatchPhraseQueryBuilder
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.TermQueryBuilder
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.exceptions.NumberOfParticipantsExceeded


interface SessionCustomElasticsearchRepository {
    fun updateNumberOfParticipants(sessionCode: Int)
    fun updateStatus(id: String, newStatus: QqStatus)
}

class SessionCustomElasticsearchRepositoryImpl(
    private val elasticCustom: CustomElasticRequests
) : SessionCustomElasticsearchRepository {

    override fun updateNumberOfParticipants(sessionCode: Int) = try {
        val script =
            "if (ctx._source.numberOfParticipants < ctx._source.limitOfParticipants ) {ctx._source.numberOfParticipants++ ;} else {ctx.op = 'noop';} "
        val response = elasticCustom.buildAndSendRequest(
            "sessions",
            TermQueryBuilder("guestCode", sessionCode),
            emptyMap(),
            script
        )

        if (response.noops > 0) throw NumberOfParticipantsExceeded() else {}
/*        if (response.updated == 0L) throw Exception() else {
        }*/
    } catch (e: Exception) {
        println("The exception was here")
        println(e)
    }

    override fun updateStatus(id: String, newStatus: QqStatus) {
        val script = "ctx._source.status = \'$newStatus\'"
        val response = elasticCustom.buildAndSendRequest(
            "sessions",
            MatchPhraseQueryBuilder("id", id),
            emptyMap(),
            script
        )

        if(response.updated == 0L) throw Exception("It was not updated")
    }
}