package pt.isel.ps.qq.controllers.unauthcontrollers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import pt.isel.ps.qq.controllers.CookieHandler
import pt.isel.ps.qq.controllers.responsebuilders.ParticipantResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.AnswersService
import pt.isel.ps.qq.service.QuizService
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris
import java.time.Duration
import javax.servlet.http.HttpServletRequest

class ParticipantInSessionController(
    private val participantService: AnswersService,
    private val sessionService: SessionService,
    private val quizService: QuizService,
    private val responseBuilder: ParticipantResponseBuilder,
    private val cookie: CookieHandler
) : UnauthMainController() {

    @PostMapping(Uris.API.Web.V1_0.NonAuth.JoinSession.ENDPOINT)
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        val participantDoc = sessionService.joinSession(input)
        val headers = HttpHeaders() //TODO: something to be done with this cookie
        headers.add(
            "Set-Cookie",
            cookie.createCookie("InSession", participantDoc.id, Duration.ofDays(7).toSeconds())
        )
        return ResponseEntity.ok().headers(headers).body(ParticipantOutputModel(participantDoc.id))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.GiveAnswer.ENDPOINT)
    fun giveAnswer(request: HttpServletRequest, @RequestBody input: GiveAnswerInputModel): ResponseEntity<Any> {
        val participantDoc = participantService.giveAnswer(input)
        val body = responseBuilder.buildGetParticipantResponse(participantDoc)
        return ResponseEntity.ok().body(body)
    }

    //TODO: create a cookie handler and move this there


    @GetMapping(Uris.API.Web.V1_0.NonAuth.GetAnswer.ENDPOINT)
    fun getParticipant(@PathVariable answerId: String, request: HttpServletRequest): ResponseEntity<Any> {
        val participantDoc = participantService.getParticipant(answerId)
        if (!sessionService.checkSessionIsLive(participantDoc.sessionId)) { //TODO: Use problem+json
            val expectedCookie = request.cookies.find { it.name == "InSession" }!!
            val headers = HttpHeaders()
            if (expectedCookie != null)
                headers.add("Set-Cookie", cookie.expireCookie(expectedCookie))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .headers(headers)
                .contentType(ProblemJson.MEDIA_TYPE)
                .body("Session Not Found")
        }
        val body = responseBuilder.buildGetParticipantResponse(participantDoc)
        return ResponseEntity.ok().body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.Quiz.SessionId.CONTROLLER_ENDPOINT)
    fun getAllQuizzesForParticipantSession(@PathVariable participantId: String): ResponseEntity<Any> {
        val participantDoc = participantService.getParticipant(participantId) //TODO: checks here may not exist anymore
        val quizzes = quizService.getAllSessionAnswersQuizzes(participantDoc.sessionId)
        val body = responseBuilder.buildGetAllQuizzesResponse(quizzes)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }
}