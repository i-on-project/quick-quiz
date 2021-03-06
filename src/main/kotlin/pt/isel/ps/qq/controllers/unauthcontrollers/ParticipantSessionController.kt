package pt.isel.ps.qq.controllers.unauthcontrollers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.controllers.CookieHandler
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.controllers.responsebuilders.ParticipantResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.exceptions.GuestSessionNotFoundException
import pt.isel.ps.qq.exceptions.IllegalStatusOperationException
import pt.isel.ps.qq.exceptions.MissingCookieException
import pt.isel.ps.qq.exceptions.SessionIllegalStatusOperationException
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.service.AnswersService
import pt.isel.ps.qq.service.HistoryService
import pt.isel.ps.qq.service.QuizService
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris
import java.time.Duration
import javax.servlet.http.HttpServletRequest


@Controller("ParticipantSessionController")
@RequestMapping(Uris.API.Web.V1_0.NonAuth.PATH)
class ParticipantSessionController(
    private val participantService: AnswersService,
    private val sessionService: SessionService,
    private val quizService: QuizService,
    private val historyService: HistoryService,
    private val responseBuilder: ParticipantResponseBuilder,
    private val cookie: CookieHandler
) {

    @PostMapping(Uris.API.Web.V1_0.NonAuth.JoinSession.ENDPOINT)
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        val participantDoc = sessionService.joinSession(input)
        val headers = HttpHeaders()
        headers.add(
            "Set-Cookie",
            cookie.createCookie("InSession", "${participantDoc.id}&${participantDoc.sessionId}", Duration.ofDays(7).toSeconds()) //TODO: Change time for this cookie
        )
        return ResponseEntity.ok().headers(headers)
            .contentType(SirenModel.MEDIA_TYPE)
            .body(responseBuilder.buildJoinSessionResponse(participantDoc.id))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.GiveAnswer.ENDPOINT)
    fun giveAnswer(request: HttpServletRequest, @RequestBody input: GiveAnswerInputModel): ResponseEntity<Any> {
        val participantDoc = participantService.giveAnswer(input)
        val body = responseBuilder.buildGetParticipantResponse(participantDoc)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    private fun expireCookieInSession(request: HttpServletRequest): HttpHeaders {
        val expectedCookie = request.cookies.find { it.name == "InSession" }
        val headers = HttpHeaders()
        if (expectedCookie != null)
            headers.add("Set-Cookie", cookie.expireCookie(expectedCookie))
        return headers
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.GetAnswer.ENDPOINT)
    fun getParticipant(@PathVariable participantId: String, request: HttpServletRequest): ResponseEntity<Any> {
        try {
            val participantDoc = participantService.getParticipant(participantId)
            if (!sessionService.checkSessionIsLive(participantDoc.sessionId)) { //TODO: Use problem+json
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .headers(expireCookieInSession(request))
                    .contentType(ProblemJson.MEDIA_TYPE)
                    .body(ProblemJson(
                        type = "SessionIllegalStatusOperation",
                        title = "The status of the session is different from STARTED",
                        status = 409,
                        instance = request.requestURI
                    ))
            }
            val body = responseBuilder.buildGetParticipantResponse(participantDoc)
            return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(e: GuestSessionNotFoundException) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .headers(expireCookieInSession(request))
                .contentType(ProblemJson.MEDIA_TYPE)
                .body(ProblemJson(
                    type = "SessionIllegalStatusOperationException",
                    title = "The status of the session is different from STARTED",
                    status = 409,
                    instance = request.requestURI
                ))
        }
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.Quiz.SessionId.CONTROLLER_ENDPOINT)
    fun getAllQuizzesForParticipantSession(@PathVariable participantId: String): ResponseEntity<Any> {
        val participantDoc = participantService.getParticipant(participantId)
        val quizzes = quizService.getAllSessionAnswersQuizzes(participantDoc.sessionId)
        val body = responseBuilder.buildGetAllQuizzesResponse(quizzes)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.IsInSession.ENDPOINT)
    fun checkInSessionStatus(request: HttpServletRequest): ResponseEntity<Any> {
        println(request.cookies)
        val expectedCookie = request.cookies?.find { it.name == "InSession" } ?: throw MissingCookieException(cookieName = "InSession")

        val v = expectedCookie.value.split("&")
        return ResponseEntity.ok()
            .contentType(SirenModel.MEDIA_TYPE)
            .body(responseBuilder.buildCheckInSessionResponse(v[0], v[1]))
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.ParticipantHistory.ENDPOINT)
    fun getParticipantHistorySession(@PathVariable participantId: String, @PathVariable sessionId: String): ResponseEntity<Any> {
        val participantHistory = historyService.getHistory(participantId, sessionId)
        val body = responseBuilder.getParticipantHistoryResponse(participantHistory)

        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.NonAuth.SessionStatus.ENDPOINT)
    fun getSessionStatus(@PathVariable participantId: String): ResponseEntity<Void> {
        val isStarted = participantService.checkSessionIsLiveNonAuth(participantId)
        if(!isStarted) throw SessionIllegalStatusOperationException(QqStatus.CLOSED)
        return ResponseEntity.noContent().build()
    }
}