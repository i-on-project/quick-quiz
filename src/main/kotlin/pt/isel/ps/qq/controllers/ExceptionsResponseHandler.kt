package pt.isel.ps.qq.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.*
import javax.servlet.http.HttpServletRequest

@Component
class ExceptionsResponseHandler(private val scope: UserInfoScope) {

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

    private fun values(id: String?, message: String?) =
        mapOf<String, Any?>("user" to scope.getUser().userName, "id" to id, "message" to message)

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: SessionNotFoundException
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionNotFoundException",
            title = "This session doesn't exist",
            status = 404,
            instance = request.requestURI,
            values = mapOf("user" to scope.getUser().userName, "id" to id, "message" to ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: QuizNotFoundException
    ): ResponseEntity<Any> {
        val map = values(id, ex.message).toMutableMap()
        map["session"] = ex.session
        return exceptionHandling(
            type = "QuizNotFoundException",
            title = "This quiz doesn't exist",
            status = 404,
            instance = request.requestURI,
            values = mapOf("user" to scope.getUser().userName, "id" to id, "message" to ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: SessionAuthorizationException
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionAuthorizationException",
            title = "You don't have authority over this session",
            status = 403,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: QuizAuthorizationException
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "QuizAuthorizationException",
            title = "You don't have authority over this quiz",
            status = 403,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: ImpossibleGenerationException
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "ImpossibleGenerationException",
            title = "Was not possible generate a pin code for your session",
            status = 409,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: SessionIllegalStatusOperationException
    ): ResponseEntity<Any> {
        val map = values(id, ex.message).toMutableMap()
        map["status"] = ex.status.toString()
        return exceptionHandling(
            type = "SessionIllegalStatusOperationException",
            title = "The session status is: ${ex.status}",
            status = 409,
            instance = request.requestURI,
            values = map
        )
    }

    fun exceptionHandle(request: HttpServletRequest, id: String, ex: AtLeast2Choices): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast2Choices",
            title = "A multiple choice question requires at least 2 choices",
            status = 400,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: AtLeast1CorrectChoice
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast1CorrectChoice",
            title = "A multiple choice question requires at least 1 of the choices to be correct",
            status = 400,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

    fun exceptionHandle(
        request: HttpServletRequest,
        id: String,
        ex: LiveSessionAlreadyExists
    ): ResponseEntity<Any> {
        return exceptionHandling(
            type = "LiveSessionAlreadyExists",
            title = "A Live Session already exists",
            status = 403,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

}