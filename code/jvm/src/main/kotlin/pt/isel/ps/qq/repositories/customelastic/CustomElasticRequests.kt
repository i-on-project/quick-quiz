package pt.isel.ps.qq.repositories.customelastic

import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.TermQueryBuilder
import org.elasticsearch.index.reindex.BulkByScrollResponse
import org.elasticsearch.index.reindex.UpdateByQueryRequest
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.springframework.stereotype.Component


interface ICustomElasticRequests {

    fun buildAndSendRequest(indexName: String, term: TermQueryBuilder, parameters: Map<String, Any>, script: String ): BulkByScrollResponse
}

@Component
class CustomElasticRequests(private val highLevelClient: RestHighLevelClient): ICustomElasticRequests{

    override fun buildAndSendRequest(indexName: String, term: TermQueryBuilder, parameters: Map<String, Any>, script: String ): BulkByScrollResponse {
        val request = UpdateByQueryRequest(indexName)
        request.setQuery(term)

        val inline = Script(
            ScriptType.INLINE,
            "painless",
            script,
            parameters
        )
        request.script = inline
        request.isRefresh = true
        return highLevelClient.updateByQuery(request, RequestOptions.DEFAULT)
    }

}