package pt.isel.ps.qq.exceptions

import java.net.URI

data class ErrorInstance(
    val method: URI,
    val instance: Any
) {
    override fun toString(): String {
        return "${method}@${instance}"
    }
}

open class ProblemJsonException(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: ErrorInstance
): Exception()