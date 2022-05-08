package pt.isel.ps.qq

import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.ps.qq.data.ProblemJson

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    private fun getURIFromWebRequest(request: WebRequest): String {
        val str = request.toString().split("=")
        val path = str[1].split(";")
        return path[0]
    }

    @ExceptionHandler(DataAccessResourceFailureException::class)
    fun handleDataAccessResourceFailureException(ex: DataAccessResourceFailureException, request: WebRequest): ResponseEntity<Any> {
        val problem = ProblemJson(
            type = "DataAccessResourceFailureException",
            title = "One of the services is currently unavailable",
            status = 502,
            instance = getURIFromWebRequest(request),
            values = mapOf("message" to ex.message)
        )
        return ResponseEntity.status(502).contentType(ProblemJson.MEDIA_TYPE).body(problem.toString())
    }
}
