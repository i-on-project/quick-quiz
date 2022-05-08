package pt.isel.ps.qq.data

import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.http.MediaType
import pt.isel.ps.qq.exceptions.ProblemJsonException

data class ProblemJson(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String? = null,
    val instance: String,
    val values: Map<String, Any?> = emptyMap()
) {
    constructor(ex: ProblemJsonException): this(
        type = ex.type, title = ex.title, status = ex.status, detail = ex.detail, instance = ex.instance.toString()
    )

    constructor(ex: DataAccessResourceFailureException, instance: String): this(
        type = "DataAccessResourceFailureException",
        title = "One of the services is currently unavailable.",
        status = 502,
        detail = "Please try again later, if the issue remains contact our support team",
        instance = instance,
        values = mapOf("message" to ex.message)
    )

    override fun toString(): String {
        val str = StringBuilder("{\"type\":\"${this.type}\",\"title\":\"${this.title}\",\"status\":${this.status},\"instance\":\"${this.instance}\"")
        if(detail != null) str.append(",\"detail\":\"${this.detail}\"")
        values.forEach {
            if(it.value == null) return@forEach
            var aux = "\"${it.value}\""
            if(it.value!!.javaClass.isPrimitive) aux = it.value.toString()
            str.append(",\"${it.key}\":${aux}")
        }
        str.append('}')
        return str.toString()
    }

    companion object {
        val MEDIA_TYPE = MediaType.parseMediaType("application/problem+json")
    }
}