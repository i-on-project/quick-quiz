package pt.isel.ps.qq.repositories.customelastic

import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.reindex.BulkByScrollResponse
import org.elasticsearch.index.reindex.DeleteByQueryRequest
import org.elasticsearch.index.reindex.UpdateByQueryRequest
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.springframework.stereotype.Component


interface IElasticCustomRequest {
    fun buildAndSendRequest(indexName: String, term: QueryBuilder, parameters: Map<String, Any>, script: String ): BulkByScrollResponse
    fun updateDocument(index: String, id: String, parameters: Map<String, Any>, script: String): UpdateResponse

    /**
     * @param value if field is primitive this map contains a single value with the content of that field, otherwise
     * this map contains the object described by field.name:value
     */
    fun updateField(index: String, id: String, field: String, value: Map<String, Any>): UpdateResponse?
}

@Component
class ElasticRequest(private val highLevelClient: RestHighLevelClient): IElasticCustomRequest{

    override fun buildAndSendRequest(indexName: String, term: QueryBuilder, parameters: Map<String, Any>, script: String ): BulkByScrollResponse {
        val request = UpdateByQueryRequest(indexName)
        request.setQuery(term)

        val inline = Script(ScriptType.INLINE, "painless", script, parameters)
        request.script = inline
        request.isRefresh = true
        return highLevelClient.updateByQuery(request, RequestOptions.DEFAULT)
    }

    override fun updateDocument(index: String, id: String, parameters: Map<String, Any>, script: String): UpdateResponse {
        val inline = Script(ScriptType.INLINE, "painless", script, parameters)
        val request = UpdateRequest(index, id).script(inline)
        return highLevelClient.update(request, RequestOptions.DEFAULT)!!
    }

    override fun updateField(index: String, id: String, field: String, value: Map<String, Any>): UpdateResponse {
        val script = "ctx._source.${field} = params.value"
        val params = mutableMapOf<String, Any>()
        if(value.isEmpty()) throw Exception("Cannot update value to empty obj")
        else if(value.count() == 1) value.forEach { params["value"] = it.value }
        else params["value"] = value
        val request = UpdateRequest(index, id).script(Script(ScriptType.INLINE, "painless", script, params))
        return highLevelClient.update(request, RequestOptions.DEFAULT)!!
    }
}