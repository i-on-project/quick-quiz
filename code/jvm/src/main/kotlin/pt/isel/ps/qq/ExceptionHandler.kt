package pt.isel.ps.qq

import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.ProblemJsonException

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(ProblemJsonException::class)
    fun handleProblemJsonException(ex: ProblemJsonException, request: WebRequest): ResponseEntity<Any> {
        logger.info("Type:${ex.type};Instance:${ex.instance}")
        return ResponseEntity.status(ex.status).contentType(ProblemJson.MEDIA_TYPE).body(ProblemJson(ex))
    }

    @ExceptionHandler(DataAccessResourceFailureException::class)
    fun handleDataAccessResourceFailureException(ex: DataAccessResourceFailureException, request: WebRequest): ResponseEntity<Any> {
        logger.info("Database Down")
        val str = request.toString().split("=")
        val path = str[1].split(";")
        return ResponseEntity.status(502).contentType(ProblemJson.MEDIA_TYPE).body(
            ProblemJson(ex = ex, instance = "${path[0]}@null")
        )
    }

    //TODO: Remove the ProblemJsonException and put here all the exceptions
}
