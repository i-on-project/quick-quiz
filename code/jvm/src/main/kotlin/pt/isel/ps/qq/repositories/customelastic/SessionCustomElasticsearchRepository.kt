package pt.isel.ps.qq.repositories.customelastic


import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.TermQueryBuilder
import org.elasticsearch.index.reindex.UpdateByQueryRequest
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.springframework.beans.factory.annotation.Autowired


interface SessionCustomElasticsearchRepository {
    fun updateNumberOfParticipants(sessionCode: Int)
}

class SessionCustomElasticsearchRepositoryImpl : SessionCustomElasticsearchRepository {

    @Autowired
    private val highLevelClient: RestHighLevelClient? = null

    override fun updateNumberOfParticipants(sessionCode: Int) = try {

        val request = UpdateByQueryRequest("sessions")
        request.setQuery(TermQueryBuilder("guestCode", sessionCode))

        val parameters: Map<String, Any> = emptyMap<String, Any>()
        val inline = Script(
            ScriptType.INLINE,
            "painless",
            "if (ctx._source.numberOfParticipants < ctx._source.limitOfParticipants ) {ctx._source.numberOfParticipants++ ;} else {ctx.op = 'noop';} ",
            parameters
        )

        request.script = inline
        request.isRefresh = true

        val response = highLevelClient!!.updateByQuery(request, RequestOptions.DEFAULT)
        val test = response.updated
        println(response.updated)
    } catch (e: Exception) {
        println(e)
    }

}