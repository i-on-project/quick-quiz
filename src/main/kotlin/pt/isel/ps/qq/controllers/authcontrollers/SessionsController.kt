package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.controllers.responsebuilders.SessionsResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.repositories.UserRepository
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.calculateLastPage
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest

@RestController("SessionsController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class SessionsController(
    private val service: SessionService,
    private val scope: UserInfoScope,
    private val userRepository: UserRepository,
    private val sessionResponse: SessionsResponseBuilder
) {

    /**
     * GET /api/web/v1.0/auth/session
     *
     * This handler obtains all the sessions of a user using pagination. The page size is declared on SessionService.kt
     *
     * Handler to get all the sessions of a user.
     * @param request injected HTTP request
     * @param page page to get
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = ListSessionSummary
     * Siren properties = list info
     * Siren entities = SessionSummary -> summary info of a session
     * Siren entities relation = self -> reference to session
     * Siren links = first -> first page of this list; last -> last page of this list
     * Siren link optional -> next -> next page of this list
     * Siren link optional -> prev -> previous page of this list
     */
    @GetMapping(Uris.API.Web.V1_0.Auth.Session.ENDPOINT)
    fun getAllSessions(request: HttpServletRequest, @RequestParam page: Int?): ResponseEntity<SirenModel> {
        val idx = page ?: 0
        val sessions = service.getAllSessions(scope.getUser().userName, idx)
        val total = service.sessionDocumentsCount(scope.getUser().userName)
        val body = sessionResponse.allSessionsResponse(
            getBaseUrlHostFromRequest(request),
            total.toInt(),
            calculateLastPage(total),
            idx,
            sessions
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    /**
     * POST /api/web/v1.0/auth/session
     *
     * This handler creates a session on the data repository 'sessions'.
     *
     * Handler to create a new session.
     * @param request injected HTTP request
     * @param input values with the session configuration
     * @return a ResponseEntity with status code 201, header location and body with a siren response
     *
     * Siren class = CreateSession
     * Siren properties = acknowledge
     * Siren links = self -> reference to session
     */
    @PostMapping(Uris.API.Web.V1_0.Auth.Session.ENDPOINT)
    fun createSession(request: HttpServletRequest, @RequestBody input: SessionInputModel): ResponseEntity<SirenModel> {
        val session = service.createSession(scope.getUser().userName, input)
        if (session.tags.isNotEmpty())
            updateUserTags(scope.getUser().userName, session.tags)
        val body = sessionResponse.createSessionResponse(session)
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Session.Id.make(session.id))
            .contentType(SirenModel.MEDIA_TYPE)
            .body(body)
    }

    private fun updateUserTags(userName: String, tags: List<String>) {
        val user = userRepository.findById(userName).get()
        var tagsAdded = false;
        tags.forEach { t ->
            if (!user.tags.contains(t)) {
                user.tags.add(t)
                tagsAdded = true
            }
        }
        if(tagsAdded) userRepository.save(user)
    }


    /**
     * DELETE /api/web/v1.0/auth/session/{id}
     *
     * This handler deletes a session from the data repository 'sessions' if exists. The user is verified for ownership
     * of the session before the delete process
     *
     * Handler to delete a session if exists.
     * @param request injected HTTP request
     * @param id id that references the session
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = DeleteSession
     * Siren properties = acknowledge
     */
    @DeleteMapping(Uris.API.Web.V1_0.Auth.Session.Id.CONTROLLER_ENDPOINT)
    fun deleteSessionById(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        //val language = request.locale ?: Locale.ENGLISH
        //return try {
            service.deleteSession(scope.getUser().userName, id)
            val body = sessionResponse.deleteSessionResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
     /*   } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    /**
     * GET /api/web/v1.0/auth/session/{id}
     *
     * Handler to get the full information of a session.
     * @param request injected HTTP request
     * @param id id that references the session
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = Session
     * Siren properties = Session values
     * Siren actions = DELETE -> delete this session; UPDATE -> update this session; ADD -> add a new quiz to this session
     * Siren action optional (status == NOT_STARTED) = GO LIVE -> make this session go live
     * Siren action optional (status == STARTED) = STOP -> shut down this session
     */
    @GetMapping(Uris.API.Web.V1_0.Auth.Session.Id.CONTROLLER_ENDPOINT)
    fun getSessionById(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        //return try {
            val session = service.getSessionValidatingTheOwner(scope.getUser().userName, id)
            val body = sessionResponse.getSessionResponse(id, session)
            return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
/*        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    /**
     * PUT /api/web/v1.0/auth/session/{id}
     *
     * Handler to edit a session. After this handler is executed successfully the session is updated with the values
     * obtained from the request payload. Only the values not null are updated, the null values stay the same.
     * @param request injected HTTP request
     * @param id id that references the session
     * @param input values to update on the session
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = EditSession
     * Siren properties = acknowledge
     * Siren links = self -> reference to session
     */
    @PutMapping(Uris.API.Web.V1_0.Auth.Session.Id.CONTROLLER_ENDPOINT)
    fun editSessionById(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody input: EditSessionInputModel
    ): ResponseEntity<Any> {
        //return try {
            val session = service.editSession(scope.getUser().userName, id, input)
            if (session.tags.isNotEmpty())
                updateUserTags(scope.getUser().userName, session.tags)
            val body = sessionResponse.editSessionResponse(getBaseUrlHostFromRequest(request), session.id)
            return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
/*        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    /**
     * POST /api/web/v1.0/auth/session/{id}/live
     *
     * This handler makes a session go live. This makes the guest code present on the session to be searchable by the
     * players and the players to join this session starting to giving answers.
     *
     * Handler to make a session go live. After this handler is executed successfully the session status is STARTED.
     * @param request inject HTTP request
     * @param id id that references the session
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = LiveSession
     * Siren properties = acknowledge
     */
    @PostMapping(Uris.API.Web.V1_0.Auth.Session.Id.Live.CONTROLLER_ENDPOINT)
    fun startSession(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        //return try {
            val code = service.makeSessionLive(scope.getUser().userName, id)
            val body = sessionResponse.startSessionResponse(code)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
      /*  } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: ImpossibleGenerationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: LiveSessionAlreadyExists) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    /**
     * POST /api/web/v1.0/auth/session/{id}/close
     *
     * This handler shuts down a session. This makes the guest code present on the session to be invalid and the answers
     * of the players to be rejected.
     *
     * Handler to shut down a session. After this handler is executed successfully the session status is CLOSED and is
     * created a new entry in the history of the user.
     * @param request inject HTTP request
     * @param id id that references the session
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = LiveSession
     * Siren properties = acknowledge
     */
    @PostMapping(Uris.API.Web.V1_0.Auth.Session.Id.Close.CONTROLLER_ENDPOINT)
    fun closeSession(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
       // return try {
            service.shutdownSession(scope.getUser().userName, id)
            val body = sessionResponse.closeSessionResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
/*        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        }*/
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Session.Id.Answers.CONTROLLER_ENDPOINT)
    fun getAllAnswers(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val participantsAndAnswers = service.getAllAnswersForSession(scope.getUser().userName, id)
        val body = sessionResponse.getAllAnswersResponse(participantsAndAnswers)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }


}