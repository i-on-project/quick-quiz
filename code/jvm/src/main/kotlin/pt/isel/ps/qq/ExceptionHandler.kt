package pt.isel.ps.qq

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.ps.qq.exceptions.ProblemJsonException

data class ProblemJson(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String
) {
    constructor(ex: ProblemJsonException): this(
        type = ex.type,
        title = ex.title,
        status = ex.status,
        detail = ex.detail,
        instance = ex.instance.toString()
    )
}

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    companion object {
        val MEDIA_TYPE = MediaType.parseMediaType("application/problem+json")
    }

    @ExceptionHandler(ProblemJsonException::class)
    fun handleProblemJsonException(ex: ProblemJsonException, request: WebRequest): ResponseEntity<Any> {
        logger.info("Type:${ex.type};Instance:${ex.instance}")

        return ResponseEntity.status(ex.status)
            .contentType(MEDIA_TYPE)
            .body(ProblemJson(ex))
    }
}