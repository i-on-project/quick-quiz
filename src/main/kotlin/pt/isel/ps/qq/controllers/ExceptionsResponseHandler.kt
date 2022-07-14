package pt.isel.ps.qq.controllers


import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.*
import javax.servlet.http.HttpServletRequest

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class ExceptionsResponseHandler(private val scope: UserInfoScope) : ResponseEntityExceptionHandler() {


    fun exceptionHandling(
        type: String,
        title: String,
        status: Int,
        instance: String,
        values: Map<String, Any?> = emptyMap(),
        detail: String? = null
    ): ResponseEntity<Any> {
        val problem = ProblemJson(
            type = type,
            title = title,
            status = status,
            instance = instance,
            values = values,
            detail = detail
        )
        return ResponseEntity.status(problem.status).contentType(ProblemJson.MEDIA_TYPE).body(problem.toString())
    }
    /**Bad Request**/
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "BadRequest",
            title = "Data os missing from request",
            status = 400,
            instance = request.contextPath,
            values = mapOf("user" to scope.getUser().userName, "message" to ex.message)
        )
    }
    /**Catch All**/
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException, headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "InternalError",
            title = "An Unknown error occurred",
            status = 500,
            instance = request.contextPath,
            values = mapOf("user" to scope.getUser().userName, "message" to ex.message)
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: org.springframework.web.bind.MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "BadRequest",
            title = "Data os missing from request",
            status = 400,
            instance = request.contextPath,
            values = mapOf("user" to scope.getUser().userName, "message" to ex.message)
        )
    }


    private fun values(id: String?, message: String?) =
        mapOf<String, Any?>("user" to scope.getUser().userName, "id" to id, "message" to message)

    @ExceptionHandler(value = [SessionNotFoundException::class])
    fun exceptionHandle(
        ex: SessionNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionNotFoundException",
            title = "This session doesn't exist",
            status = 404,
            instance = request.contextPath,
            values = mapOf("user" to scope.getUser().userName, "message" to ex.message)
        )
    }


    @ExceptionHandler(value = [QuizNotFoundException::class])
    fun exceptionHandle(
        ex: QuizNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "QuizNotFoundException",
            title = "This quiz doesn't exist",
            status = 404,
            instance = request.contextPath,
            values = mapOf("user" to scope.getUser().userName, "message" to ex.message)
        )
    }

    @ExceptionHandler(value = [SessionAuthorizationException::class])
    fun exceptionHandle(
        ex: SessionAuthorizationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionAuthorizationException",
            title = "You don't have authority over this session",
            status = 403,
            instance = request.contextPath,
            values = values("error", ex.message)
        )
    }

    @ExceptionHandler(value = [QuizAuthorizationException::class])
    fun exceptionHandle(
        ex: QuizAuthorizationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "QuizAuthorizationException",
            title = "You don't have authority over this quiz",
            status = 403,
            instance = request.contextPath,
            values = values("error", ex.message)
        )
    }

    @ExceptionHandler(value = [ImpossibleGenerationException::class])
    fun exceptionHandle(
        ex: ImpossibleGenerationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "ImpossibleGenerationException",
            title = "Was not possible generate a pin code for your session",
            status = 409,
            instance = request.contextPath,
            values = values("error", ex.message)
        )
    }

    @ExceptionHandler(value = [SessionIllegalStatusOperationException::class])
    fun exceptionHandle(
        ex: SessionIllegalStatusOperationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val map = values("error", ex.message).toMutableMap()
        map["status"] = ex.status.toString()
        return exceptionHandling(
            type = "SessionIllegalStatusOperationException",
            title = "The session status is: ${ex.status}",
            status = 409,
            instance = request.contextPath,
            values = map
        )
    }

    @ExceptionHandler(value = [AtLeast2Choices::class])
    fun handleAtLeast2Choices(
        ex: AtLeast2Choices,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast2Choices",
            title = "A multiple choice question requires at least 2 choices",
            status = 400,
            instance = request.contextPath,
            values = values("id", ex.message)
        )
    }

    @ExceptionHandler(value = [AtLeast1CorrectChoice::class])
    fun exceptionHandle(
        ex: AtLeast1CorrectChoice,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast1CorrectChoice",
            title = "A multiple choice question requires at least 1 of the choices to be correct",
            status = 400,
            instance = request.contextPath,
            values = values("error", ex.message)
        )
    }

    @ExceptionHandler(value = [LiveSessionAlreadyExists::class])
    fun exceptionHandle(
        ex: LiveSessionAlreadyExists,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "LiveSessionAlreadyExists",
            title = "A Live Session already exists",
            status = 403,
            instance = request.contextPath,
            values = values("error", ex.message)
        )
    }

    @ExceptionHandler(value = [DataAccessResourceFailureException::class]) /*TODO: Needs to be handled in the filter*/
    fun exceptionHandle(
        ex: DataAccessResourceFailureException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "DataAccessResourceFailureException",
            title = "One of the services is currently unavailable",
            status = 502,
            instance = request.contextPath,
            values = mapOf("message" to ex.message)
        )
    }


}