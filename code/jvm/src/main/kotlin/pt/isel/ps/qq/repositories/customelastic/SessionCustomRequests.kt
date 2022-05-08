package pt.isel.ps.qq.repositories.customelastic


import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.TermQueryBuilder
import org.elasticsearch.index.reindex.BulkByScrollResponse
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.exceptions.NumberOfParticipantsExceeded
import pt.isel.ps.qq.exceptions.SessionAuthorizationException
import pt.isel.ps.qq.exceptions.SessionIllegalStatusOperationException
import pt.isel.ps.qq.exceptions.SessionNotFoundException
import pt.isel.ps.qq.utils.getCurrentTimeSeconds


interface SessionCustomRequests {
    fun updateNumberOfParticipants(sessionCode: Int)
    fun updateStatus(id: String, newStatus: QqStatus)
    fun makeSessionGoLive(id: String, owner: String, guestCode: Int)
    fun shutDownSession(id: String, owner: String)
    fun updateQuizzes(id: String, owner: String, quizId: String, action: CustomRequestUpdateQuizAction)
}

enum class CustomRequestUpdateQuizAction(val script: String) {
    ADD("add"), REMOVE("remove")
}

class SessionCustomRequestsImpl(
    private val elasticCustom: ElasticRequest
) : SessionCustomRequests {

    override fun updateNumberOfParticipants(sessionCode: Int) {
        val script = "if (ctx._source.numberOfParticipants < ctx._source.limitOfParticipants ) {ctx._source.numberOfParticipants++ ;} else {ctx.op = 'noop';} "
        val query = TermQueryBuilder("guestCode", sessionCode)
        val response = request(query, emptyMap(), script)
        if (response.noops > 0) throw NumberOfParticipantsExceeded()
        if (response.updated == 0L) throw Exception("it was not updated")
    }

    override fun updateStatus(id: String, newStatus: QqStatus) {
        request(id, "status", mapOf("" to newStatus))
    }

    override fun makeSessionGoLive(id: String, owner: String, guestCode: Int) {
        val script = "if(ctx._source.owner == '$owner') { " +
                "if(ctx._source.status == '${QqStatus.NOT_STARTED}') {" +
                "ctx._source.status = '${QqStatus.STARTED}'; " +
                "ctx._source.liveDate = ${getCurrentTimeSeconds()}; " +
                "ctx._source.guestCode = $guestCode } " +
                "else throw new Exception('status:'+ctx._source.status); } " +
                "else throw new Exception('owner');"
        try {
            val response = request(id, emptyMap(), script)
            if(response != null) {
                throw Exception(response.getResult.toString())
            }
        } catch(ex: ElasticsearchException) {
            parseElasticsearchException(ex, QqStatus.NOT_STARTED)
        }
    }

    override fun shutDownSession(id: String, owner: String) {
        val script = "if(ctx._source.owner == '$owner') { " +
                "if(ctx._source.status == '${QqStatus.STARTED}') {" +
                "ctx._source.status = '${QqStatus.CLOSED}'; }" +
                "else throw new Exception('status:'+ctx._source.status); } " +
                "else throw new Exception('owner');"
        try {
            val response = request(id, emptyMap(), script)
            if(response != null) {
                throw Exception(response.getResult.toString())
            }
        } catch(ex: ElasticsearchException) {
            parseElasticsearchException(ex, QqStatus.STARTED)
        }
    }

    private fun parseElasticsearchException(ex: ElasticsearchException, status: QqStatus) {
        val cause = ex.cause?.message!!.split('[')[1].split(',')[1].split('=')[1].split(":")
        if(cause[0] == "status") throw SessionIllegalStatusOperationException(QqStatus.valueOf(cause[1]), "To perform this operation the session status can only be $status")
        if(cause[0] == "owner") throw SessionAuthorizationException()
    }

    override fun updateQuizzes(id: String, owner: String, quizId: String, action: CustomRequestUpdateQuizAction) {
        val script = "if(ctx._source.owner == '$owner') { if(ctx._source.status == '${QqStatus.NOT_STARTED}') ctx._source.quizzes.${action.script}(params.quizId); else throw Exception('status:'+ctx._source.status); } else throw new Exception('owner');"
        val params = mapOf("quizId" to quizId)
        try {
            val response = request(id, params, script)
            if(response != null) throw Exception(response.getResult.toString())
        } catch(ex: ElasticsearchException) {
            parseElasticsearchException(ex, QqStatus.NOT_STARTED)
        }
    }

    private fun request(query: QueryBuilder, params: Map<String, Any>, script: String): BulkByScrollResponse {
        return elasticCustom.buildAndSendRequest("sessions", query, params, script)
    }
    private fun request(id: String, params: Map<String, Any>, script: String): UpdateResponse? {
        val response = elasticCustom.updateDocument("sessions", id, params, script)
        return if(validateResponse(response)) null
        else response
    }
    private fun request(id: String, field: String, value: Map<String, Any>): UpdateResponse? {
        val response = elasticCustom.updateField("sessions", id, field, value)
        return if(validateResponse(response)) null
        else response
    }

    private fun validateResponse(response: UpdateResponse): Boolean {
        return when(response.status().status) {
            200 -> true
            404 -> throw SessionNotFoundException()
            400 -> false
            else -> throw Exception("Unexpected Error validating response from elastic")
        }
    }
}