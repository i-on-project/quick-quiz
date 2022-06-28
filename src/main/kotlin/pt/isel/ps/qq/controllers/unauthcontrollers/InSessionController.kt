package pt.isel.ps.qq.controllers.unauthcontrollers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import java.time.Duration
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

class InSessionController(private val dataService: DataService) : UnauthMainController() {

    @PostMapping(Uris.API.Web.V1_0.NonAuth.JoinSession.ENDPOINT)
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        val ansDoc = dataService.joinSession(input)
        val headers = HttpHeaders() //TODO: insessuion COOKIE
        headers.add("Set-Cookie", "InSession=${ansDoc.id}; Max-Age=${Duration.ofDays(7).toSeconds()}; Path=/; Secure; ") // SameSite=Strict
        return ResponseEntity.ok().headers(headers).body(ParticipantOutputModel(ansDoc.id))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.GiveAnswer.ENDPOINT)
    fun giveAnswer(request: HttpServletRequest, @RequestBody input: GiveAnswerInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(dataService.giveAnswer(input))
    }
    //TODO: create a cookie handler and move this there
    private fun expireCookie(cookie: Cookie): String {
        val builder = StringBuilder("${cookie.name}=;")
        builder.append("Expires=Thu, 01 Jan 1970 00:00:01 GMT;")
        builder.append("Path=/;")
        builder.append("Secure;")
        builder.append("HttpOnly;")
        return builder.toString()
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.GetAnswer.ENDPOINT)
    fun getAnswer(@PathVariable answerId: String, request: HttpServletRequest): ResponseEntity<Any> {
        val ansDoc = dataService.getAnswer(answerId)
        if(!dataService.checkSessionIsLive(ansDoc.sessionId)) {
            println("Session not found")
            val cookie = request.cookies.find { it.name == "InSession" }!!
            val headers = HttpHeaders()
            headers.add("Set-Cookie", expireCookie(cookie))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).contentType(ProblemJson.MEDIA_TYPE).body("Session Not Found")
        }

        return ResponseEntity.ok().body(ansDoc)
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.Quiz.SessionId.CONTROLLER_ENDPOINT)
    fun getAllQuizzesForAnswerSession(@PathVariable answerId: String): ResponseEntity<Any> {
        val ansDoc = dataService.getAnswer(answerId) //TODO: checks here
        val quizzes = dataService.getAllSessionAnswersQuizzes(ansDoc.sessionId)

        val body = SirenModel(
            clazz = listOf("Quiz"),
            properties = ListInfo(size = quizzes.size, total = quizzes.size), //TODO: Output model required
            entities = quizzes.map {
                SirenEntity(
                    clazz = listOf("quizzes"),
                    rel = listOf("self"),
                    properties = it
                )
            }
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }
}