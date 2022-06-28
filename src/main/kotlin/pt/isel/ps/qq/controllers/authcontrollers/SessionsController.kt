package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.data.docs.QqStatus
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest

@RestController("SessionsController")
class SessionsController(private val service: DataService,
                         private val scope: UserInfoScope,
                         private val exHandler: ExceptionsResponseHandler
) : AuthMainController() {

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
        val list = service.getAllSessions(scope.getUser().userName, idx)
        val host = getBaseUrlHostFromRequest(request)
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.Session.url(host, 0)))
        val total = service.sessionDocumentsCount()
        val lastPage = calculateLastPage(total)
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.Session.url(host, lastPage)))
        if (idx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Session.make(idx + 1).toString()))
        }
        if (idx > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.Session.make(idx - 1).toString()))
        }
        val actionList: MutableList<SirenAction> = mutableListOf(
            SirenAction(
                name = "Create-Session",
                title = "Create",
                method = SirenSupportedMethods.POST,
                href = Uris.API.Web.V1_0.Auth.Session.PATH
            )
            /*SirenAction( TODO: what can be done here
                name = "GoLive-Session",
                title = "Start",
                method = SirenSupportedMethods.POST,
                href = Uris.API.Web.V1_0.Auth.Session.Id.Live.make(id).toString()
            )*/
        )

        val body = SirenModel(
            clazz = listOf("ListSessionSummary"),
            properties = ListInfo(size = list.size, total = total.toInt()),
            entities = list.map {
                SirenEntity(
                    clazz = listOf("SessionSummary"),
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(it.id).toString(),
                    properties = SessionSummaryOutputModel(it),
                    links = listOf(
                        SirenLink(
                            rel = listOf("self", "delete"),
                            title = "Delete",
                            type = SirenSupportedMethods.DELETE.toString(),
                            href = Uris.API.Web.V1_0.Auth.Session.Id.make(it.id).toString()
                        ),
                        SirenLink(
                            rel = listOf("self", "start"),
                            title = "Start",
                            type = SirenSupportedMethods.POST.toString(),
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Live.make(it.id).toString()
                        ),
                        SirenLink(
                            rel = listOf("self", "close"),
                            title = "Close",
                            type = SirenSupportedMethods.POST.toString(),
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Close.make(it.id).toString()
                        )

                    )
                )
            },
            actions = actionList,
            links = links
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
        val doc = service.createSession(scope.getUser().userName, input)

        val body = SirenModel(
            clazz = listOf("CreateSession"),
            properties = doc,
            title = "Session successfully created.",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id).toString()
                )
            )
        )
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id)).contentType(SirenModel.MEDIA_TYPE)
            .body(body)
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
        return try {
            service.deleteSession(scope.getUser().userName, id)
            val body = SirenModel(
                clazz = listOf("DeleteSession"), properties = Acknowledge.TRUE, title = "Session successfully deleted."
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }
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
        return try {
            val doc = service.getSessionValidatingTheOwner(scope.getUser().userName, id)
            val host = getBaseUrlHostFromRequest(request)
            val actionList: MutableList<SirenAction> = mutableListOf(
                SirenAction(
                    name = "Delete-Session",
                    title = "Delete",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(id).toString()
                ), SirenAction(
                    name = "Update-Session",
                    title = "Edit",
                    method = SirenSupportedMethods.PUT,
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(id).toString()
                ), SirenAction(
                    name = "Add-Quiz",
                    title = "New quiz",
                    method = SirenSupportedMethods.POST,
                    href = Uris.API.Web.V1_0.Auth.Session.Id.Quiz.make(id).toString()
                )
/*                , SirenAction(
                    name = "Remove-Quiz",
                    title = "Remove quiz",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Session.Id.Quiz.make(id).toString()
                )*/
            )
            when (doc.status) {
                QqStatus.STARTED -> {
                    actionList.add(
                        SirenAction(
                            name = "Stop-Session",
                            title = "Stop",
                            method = SirenSupportedMethods.POST,
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Close.make(id).toString()
                        )
                    )
                }
                QqStatus.NOT_STARTED -> {
                    actionList.add(
                        SirenAction(
                            name = "GoLive-Session",
                            title = "Start",
                            method = SirenSupportedMethods.POST,
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Live.make(id).toString()
                        )
                    )
                }
                else -> {} //do nothing
            }
            val body = SirenModel(
                clazz = listOf("Session"),
                properties = SessionOutputModel(doc), // todo: entities = listOf(SirenEntity.quizzesSirenEntity(doc.id),SirenEntity.userSirenEntity(doc.owner)),
                actions = actionList,
                links = listOf(
                    SirenLink(
                        listOf("List", "Quiz"),
                        "Quizzes",
                        listOf("related"),
                        Uris.API.Web.V1_0.Auth.Quiz.SessionId.make(doc.id).toString()
                    ),
                    SirenLink(
                        listOf("List", "Answers"),
                        "Answers",
                        listOf("related"),
                        Uris.API.Web.V1_0.Auth.Session.Id.Answers.make(doc.id).toString()
                    )
                )
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }
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
        return try {
            val doc = service.editSession(scope.getUser().userName, id, input)
            val body = SirenModel(
                clazz = listOf("EditSession"), properties = Acknowledge.TRUE, links = listOf(
                    SirenLink(
                        rel = listOf("self"),
                        href = Uris.API.Web.V1_0.Auth.Session.Id.url(getBaseUrlHostFromRequest(request), doc.id)
                    )
                ), title = "Session successfully updated."
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        }
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
        return try {
            val code = service.makeSessionLive(scope.getUser().userName, id)
            val body = SirenModel(
                clazz = listOf("LiveSession"),
                properties = LiveSession(code.toString().padStart(9, '0')),
                title = "Session went live successfully."
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: ImpossibleGenerationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: LiveSessionAlreadyExists) {
            exHandler.exceptionHandle(request, id, ex)
        }
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
        return try {
            service.shutdownSession(scope.getUser().userName, id)
            val body = SirenModel(
                clazz = listOf("LiveSession"), properties = Acknowledge.TRUE, title = "Session was closed successfully."
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        }
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Session.Id.Answers.CONTROLLER_ENDPOINT)
    fun getAllAnswers(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val list = service.getAllAnswersForSession(scope.getUser().userName, id)

        val body = SirenModel(
            clazz = listOf("ListAnswersSummary"),
            properties = ListInfo(size = list.size, total = list.size),
            entities = list.map {
                SirenEntity(
                    clazz = listOf("AnswerSummary"),
                    rel = listOf("self"),
                    //href = Uris.API.Web.V1_0.Auth.Session.Id.make(it.id).toString(),
                    properties = AnswersOutputModel(it), //TODO

                )
            }
        )
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
        @RequestBody input: AddQuizToSessionInputModel
    ): ResponseEntity<Any> {
        return try {
            val quiz = service.addQuizToSession(scope.getUser().userName, id, input)
            val host = getBaseUrlHostFromRequest(request)
            val body = SirenModel(
                clazz = listOf("Quiz"),
                properties = Acknowledge.TRUE,
                entities = listOf(
                    SirenEntity(
                        rel = listOf("session"), links = listOf(
                            SirenLink(
                                rel = listOf("self"), href = Uris.API.Web.V1_0.Auth.Session.Id.url(host, quiz.sessionId)
                            )
                        )
                    )
                ),
                title = "Quiz was added successfully",
                links = listOf(
                    SirenLink(
                        rel = listOf("self"),
                        href = Uris.API.Web.V1_0.Auth.Quiz.Id.url(host, quiz.id)
                    )
                )
            )
            ResponseEntity.created(Uris.API.Web.V1_0.Auth.Quiz.Id.make(quiz.id)).contentType(SirenModel.MEDIA_TYPE)
                .body(body)
        } catch (ex: SessionNotFoundException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionAuthorizationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: SessionIllegalStatusOperationException) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: AtLeast2Choices) {
            exHandler.exceptionHandle(request, id, ex)
        } catch (ex: AtLeast1CorrectChoice) {
            exHandler.exceptionHandle(request, id, ex)
        }
    }

}