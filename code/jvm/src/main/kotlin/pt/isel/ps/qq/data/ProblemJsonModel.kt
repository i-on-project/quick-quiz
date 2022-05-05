package pt.isel.ps.qq.data

import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.http.MediaType
import pt.isel.ps.qq.exceptions.ProblemJsonException

data class ProblemJson(
    val type: String, val title: String, val status: Int, val detail: String, val instance: String, val message: Any? = null
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
        message = ex.message
    )

    override fun toString(): String {
        return "{\"type\":\"${this.type}\",\"title\":\"${this.title}\",\"status\":${this.status},\"detail\":\"${this.detail}\",\"instance\":\"${this.instance}\",\"message\":\"${message}\"}"
    }

    companion object {
        val MEDIA_TYPE = MediaType.parseMediaType("application/problem+json")
    }
}