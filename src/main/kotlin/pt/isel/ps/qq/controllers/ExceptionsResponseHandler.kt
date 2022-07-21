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
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.*

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
        request as ServletWebRequest
        return exceptionHandling(
            type = "BadRequest",
            title = "Data is missing from request",
            status = 400,
            instance = request.request.requestURI
        )
    }
    /**Catch All**/
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException, headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "InternalError",
            title = "An Unknown error occurred",
            status = 500,
            instance = request.request.requestURI
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: org.springframework.web.bind.MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "BadRequest",
            title = "Data is missing from request",
            status = 400,
            instance = request.request.requestURI
        )
    }

    @ExceptionHandler(value = [Exception::class])
    fun exceptionHandle(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "InternalError",
            title = "An Unknown error occurred",
            status = 500,
            instance = request.request.requestURI
        )
    }
    private fun values(id: String? = null, message: String?= null) =
        mapOf<String, Any?>("user" to scope.getUserOrNull()?.userName, "id" to id, "message" to message)

    @ExceptionHandler(value = [SessionNotFoundException::class])
    fun exceptionHandle(
        ex: SessionNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "SessionNotFoundException",
            title = "This session doesn't exist",
            status = 404,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }


    @ExceptionHandler(value = [QuizNotFoundException::class])
    fun exceptionHandle(
        ex: QuizNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "QuizNotFoundException",
            title = "This quiz doesn't exist",
            status = 404,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [SessionAuthorizationException::class])
    fun exceptionHandle(
        ex: SessionAuthorizationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "SessionAuthorizationException",
            title = "You don't have authority over this session",
            status = 403,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [QuizAuthorizationException::class])
    fun exceptionHandle(
        ex: QuizAuthorizationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "QuizAuthorizationException",
            title = "You don't have authority over this quiz",
            status = 403,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [ImpossibleGenerationException::class])
    fun exceptionHandle(
        ex: ImpossibleGenerationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "ImpossibleGenerationException",
            title = "Was not possible generate a pin code for your session",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [SessionIllegalStatusOperationException::class])
    fun exceptionHandle(
        ex: SessionIllegalStatusOperationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "SessionIllegalStatusOperationException",
            title = "The session status is: ${ex.status}",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values("status", ex.status.toString())
        )
    }

    @ExceptionHandler(value = [AtLeast2Choices::class])
    fun handleAtLeast2Choices(
        ex: AtLeast2Choices,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "AtLeast2Choices",
            title = "A multiple choice question requires at least 2 choices",
            status = 400,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [AtLeast1CorrectChoice::class])
    fun exceptionHandle(
        ex: AtLeast1CorrectChoice,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "AtLeast1CorrectChoice",
            title = "A multiple choice question requires at least 1 of the choices to be correct",
            status = 400,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [LiveSessionAlreadyExists::class])
    fun exceptionHandle(
        ex: LiveSessionAlreadyExists,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "LiveSessionAlreadyExists",
            title = "A Live Session already exists",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [DataAccessResourceFailureException::class]) /*TODO: Needs to be handled in the filter*/
    fun exceptionHandle(
        ex: DataAccessResourceFailureException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "DataAccessResourceFailureException",
            title = "One of the services is currently unavailable",
            status = 502,
            instance = request.request.requestURI,
            values = mapOf("message" to ex.message)
        )
    }

    @ExceptionHandler(value = [UserAlreadyExistsException::class])
    fun exceptionHandle(
        ex: UserAlreadyExistsException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "UserAlreadyExists",
            title = "A user with this email already exists",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [PendingValidationException::class])
    fun exceptionHandle(
        ex: PendingValidationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "PendingValidation",
            title = "Please check your email to validate your user",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun exceptionHandle(
        ex: UserNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "UserNotFound",
            title = "The email is not registered.",
            status = 404,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [TokenExpiredException::class])
    fun exceptionHandle(
        ex: TokenExpiredException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "TokenExpired",
            title = "The token is invalid, request a new email to be sent",
            status = 400,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [GuestSessionNotFoundException::class])
    fun exceptionHandle(
        ex: GuestSessionNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "GuestSessionNotFound",
            title = "This participant doesn't exist",
            status = 404,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }

    @ExceptionHandler(value = [MissingCookieException::class])
    fun exceptionHandle(
        ex: MissingCookieException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request as ServletWebRequest
        return exceptionHandling(
            type = "MissingCookieException",
            title = "Missing cookie ${ex.cookieName}",
            status = 409,
            instance = request.request.requestURI,
            detail = ex.message,
            values = values()
        )
    }
}