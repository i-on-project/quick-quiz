package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.controllers.responsebuilders.QuizzesresponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.QuizService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller("QuizzesController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class QuizzesController(
    private val service: QuizService,
    private val scope: UserInfoScope,
    private val exHandler: ExceptionsResponseHandler,
    private val quizzesResponseBuilder: QuizzesresponseBuilder
) {

    /**
     * DELETE /api/web/v1.0/auth/quiz/{id}
     *
     * Handler to remove a quiz from the session. The quiz is only removed if the session have status NOT_STARTED
     * @param request injected HTTP request
     * @param id id that references the quiz
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = RemoveQuizFromSession
     * Siren properties = acknowledge
     */
    @DeleteMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.CONTROLLER_ENDPOINT)
    fun removeQuizFromSession(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        //return try {
        service.removeQuizFromSession(scope.getUser().userName, id)
        val body = quizzesResponseBuilder.removeQuizResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
/*        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: QuizNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: QuizAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    /**
     * PUT /api/web/v1.0/auth/quiz/{id}
     *
     * Handler to edit a quiz from the session. The quiz is only edited if the session have status NOT_STARTED
     * @param request injected HTTP request
     * @param id id that references the quiz
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = TODO
     */
    @PutMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.CONTROLLER_ENDPOINT)
    fun editQuiz(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody input: EditQuizInputModel
    ): ResponseEntity<Any> {
        // return try {
        service.editQuiz(scope.getUser().userName, id, input)
        val body = quizzesResponseBuilder.editQuizResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)

    }


    @PutMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.UpdateStatus.CONTROLLER_ENDPOINT)
    fun updateQuizStatus(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody input: UpdateQuizStatusInputModel
    ): ResponseEntity<Any> {
        service.updateQuizStatus(scope.getUser().userName, id, input)
        val body = quizzesResponseBuilder.updateQuizStatusResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.CONTROLLER_ENDPOINT)
    fun getQuizFullInformation(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val quiz = service.getQuizValidatingOwner(scope.getUser().userName, id)
        val body = quizzesResponseBuilder.getQuizResponse(quiz)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    /*TODO: Make Pageable*/
    @GetMapping(Uris.API.Web.V1_0.Auth.Quiz.SessionId.CONTROLLER_ENDPOINT)
    fun getAllQuizzesForSession(@PathVariable sessionId: String): ResponseEntity<Any> {
        val quizzes = service.getAllSessionQuizzes(sessionId)
        val body = quizzesResponseBuilder.getAllQuizzesForSessionResponse(quizzes)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    /**
     * POST /api/web/v1.0/auth/session/{id}/quiz
     *
     * This handler creates a new quiz and adds it to the session, unless the session is already closed.
     *
     * Handler to create a new quiz and add it to the session. The default status of the newly created quiz is
     * NOT_STARTED, even if the session is already started.
     * @param request injected HTTP request
     * @param id id that references the session
     * @return a ResponseEntity with status code 201, header location and body with a siren response
     */
    @PostMapping(Uris.API.Web.V1_0.Auth.Session.Id.Quiz.CONTROLLER_ENDPOINT)
    fun addQuizToSession(
        request: HttpServletRequest,
        @PathVariable id: String,
        @Valid @RequestBody input: AddQuizToSessionInputModel
    ): ResponseEntity<Any> {
        // return try {
        val quiz = service.addQuizToSession(scope.getUser().userName, id, input)
        val body = quizzesResponseBuilder.addQuizzResponse(getBaseUrlHostFromRequest(request), quiz.sessionId, quiz.id)
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Quiz.Id.make(quiz.id)).contentType(SirenModel.MEDIA_TYPE)
            .body(body)
        /* } catch (ex: SessionNotFoundException) {
             exHandler.exceptionHandle(request, id, ex)
         } catch (ex: SessionAuthorizationException) {
             exHandler.exceptionHandle(request, id, ex)
         } catch (ex: SessionIllegalStatusOperationException) {
             exHandler.exceptionHandle(request, id, ex)
         } catch (ex: AtLeast2Choices) {
             exHandler.exceptionHandle(request, id, ex)
         } catch (ex: AtLeast1CorrectChoice) {
             exHandler.exceptionHandle(request, id, ex)
         }*/
    }

}