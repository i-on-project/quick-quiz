package pt.isel.ps.qq.repositories.customelastic


import org.elasticsearch.index.query.TermQueryBuilder
import pt.isel.ps.qq.exceptions.NumberOfParticipantsExceeded


interface SessionCustomElasticsearchRepository {
    fun updateNumberOfParticipants(sessionCode: Int)
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

        if (response.noops > 0) throw NumberOfParticipantsExceeded()
        if (response.updated != 0L) throw Exception() else {
        }
    } catch (e: Exception) {
        println(e)
    }
}