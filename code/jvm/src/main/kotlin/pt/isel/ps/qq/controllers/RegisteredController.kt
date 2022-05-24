package pt.isel.ps.qq.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionQuizDoc
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

/**
 * This controller is responsible to handle the requests that require authentication.
 * In this class there is a value scope that retains the information about the user. This value is injected by the
 * UserFilter that validates the user.
 */

@RestController
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class RegisteredController(
    private val service: DataService,
    private val authService: AuthenticationService,
    private val scope: UserInfoScope
) {

    private fun calculateLastPage(total: Long): Int {
        return ((total.toDouble() / DataService.PAGE_SIZE) + 0.5).toInt()
    }

    private fun expireCookie(cookie: Cookie): String {
        val builder = StringBuilder("${cookie.name}=;")
        builder.append("Expires=Thu, 01 Jan 1970 00:00:01 GMT;")
        builder.append("Path=/;")
        builder.append("Secure;")
        builder.append("HttpOnly;")
        return builder.toString()
    }

    @PostMapping(Uris.API.Web.V1_0.Auth.Logout.ENDPOINT)
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        val cookie = request.cookies.find { it.name == "Authorization" }!!
        val headers = HttpHeaders()
        authService.logout(scope.getUser().userName)
        headers.add("Set-Cookie", expireCookie(cookie))
        return ResponseEntity.ok().headers(headers).build()
    }

    private fun exceptionHandling(type: String, title: String, status: Int, instance: String, values: Map<String, Any?> = emptyMap(), detail: String? = null): ResponseEntity<Any> {
        val problem = ProblemJson(type = type, title = title, status = status, instance = instance, values = values, detail = detail)
        return ResponseEntity.status(problem.status).contentType(ProblemJson.MEDIA_TYPE).body(problem.toString())
    }

    private fun values(id: String?, message: String?) = mapOf<String, Any?>("user" to scope.getUser().userName, "id" to id, "message" to message)

    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: SessionNotFoundException): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionNotFoundException",
            title = "This session doesn't exist",
            status = 404,
            instance = request.requestURI,
            values = mapOf("user" to scope.getUser().userName, "id" to id, "message" to ex.message)
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: QuizNotFoundException): ResponseEntity<Any> {
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
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: SessionAuthorizationException): ResponseEntity<Any> {
        return exceptionHandling(
            type = "SessionAuthorizationException",
            title = "You don't have authority over this session",
            status = 403,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: QuizAuthorizationException): ResponseEntity<Any> {
        return exceptionHandling(
            type = "QuizAuthorizationException",
            title = "You don't have authority over this quiz",
            status = 403,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: ImpossibleGenerationException): ResponseEntity<Any> {
        return exceptionHandling(
            type = "ImpossibleGenerationException",
            title = "Was not possible generate a pin code for your session",
            status = 409,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: SessionIllegalStatusOperationException): ResponseEntity<Any> {
        val map = values(id, ex.message).toMutableMap()
        map["status"] = ex.status
        return exceptionHandling(
            type = "SessionIllegalStatusOperationException",
            title = "The session status is: ${ex.status}",
            status = 409,
            instance = request.requestURI,
            values = map
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: AtLeast2Choices): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast2Choices",
            title = "A multiple choice question requires at least 2 choices",
            status = 400,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }
    private fun exceptionHandle(request: HttpServletRequest, id: String, ex: AtLeast1CorrectChoice): ResponseEntity<Any> {
        return exceptionHandling(
            type = "AtLeast1CorrectChoice",
            title = "A multiple choice question requires at least 1 of the choices to be correct",
            status = 400,
            instance = request.requestURI,
            values = values(id, ex.message)
        )
    }

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
        if(idx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Session.make(idx + 1).toString()))
        }
        if(idx > 0) {
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
                            href = Uris.API.Web.V1_0.Auth.Session.Id.make(it.id).toString()),
                        SirenLink(
                            rel = listOf("self", "start"),
                            title = "Start",
                            type = SirenSupportedMethods.POST.toString(),
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Live.make(it.id).toString()),
                        SirenLink(
                            rel = listOf("self", "close"),
                            title = "Close",
                            type = SirenSupportedMethods.POST.toString(),
                            href = Uris.API.Web.V1_0.Auth.Session.Id.Close.make(it.id).toString())

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
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id)).contentType(SirenModel.MEDIA_TYPE).body(body)
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
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
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
            when(doc.status) {
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
                links = listOf(SirenLink(listOf("List", "Quiz"), "Quizzes", listOf("related"),Uris.API.Web.V1_0.Auth.Quiz.SessionId.make(doc.id).toString()))
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
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
    fun editSessionById(request: HttpServletRequest, @PathVariable id: String, @RequestBody input: EditSessionInputModel): ResponseEntity<Any> {
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
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
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
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: ImpossibleGenerationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
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
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
        }
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
    fun addQuizToSession(request: HttpServletRequest, @PathVariable id: String, @RequestBody input: AddQuizToSessionInputModel): ResponseEntity<Any> {
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
            ResponseEntity.created(Uris.API.Web.V1_0.Auth.Quiz.Id.make(quiz.id)).contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast2Choices) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast1CorrectChoice) {
            exceptionHandle(request, id, ex)
        }
    }

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
        return try {
            service.removeQuizFromSession(scope.getUser().userName, id)
            val body = SirenModel(
                clazz = listOf("DeleteSession"), properties = Acknowledge.TRUE, title = "Session successfully deleted."
            )
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizAuthorizationException) {
            exceptionHandle(request, id, ex)
        }
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
    fun editQuiz(request: HttpServletRequest, @PathVariable id: String, @RequestBody input: EditQuizInputModel): ResponseEntity<Any> {
        return try {
            service.editQuiz(scope.getUser().userName, id, input)
            val body = SirenModel(clazz = listOf("TODO"))
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast2Choices) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast1CorrectChoice) {
            exceptionHandle(request, id, ex)
        }
    }

    @PutMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.UpdateStatus.CONTROLLER_ENDPOINT)
    fun updateQuizStatus(request: HttpServletRequest, @PathVariable id: String, @RequestBody input: UpdateQuizStausInputModel): ResponseEntity<Any> {
        return try {
            service.updateQuizStatus(scope.getUser().userName, id, input)
            val body = SirenModel(clazz = listOf("TODO"))
            ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
        } catch(ex: SessionNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: SessionIllegalStatusOperationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizNotFoundException) {
            exceptionHandle(request, id, ex)
        } catch(ex: QuizAuthorizationException) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast2Choices) {
            exceptionHandle(request, id, ex)
        } catch(ex: AtLeast1CorrectChoice) {
            exceptionHandle(request, id, ex)
        }
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Quiz.Id.CONTROLLER_ENDPOINT)
    fun getQuizFullInformation(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val quiz = service.getQuizValidatingOwner(scope.getUser().userName, id)
        val body = SirenModel(
            clazz = listOf("Quiz"), //TODO: add actions update
            properties = quiz
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    /*TODO: Make Pageable*/
    @GetMapping(Uris.API.Web.V1_0.Auth.Quiz.SessionId.CONTROLLER_ENDPOINT)
    fun getAllQuizzesForSession(@PathVariable sessionid: String): ResponseEntity<Any> {
        val quizzes = service.getAllSessionQuizzes(sessionid)

/*                , SirenAction(
                    name = "Remove-Quiz",
                    title = "Remove quiz",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Session.Id.Quiz.make(id).toString()
                )*/

        val body = SirenModel(
            clazz = listOf("Quiz"),
            properties = ListInfo(size = quizzes.size, total = quizzes.size),
            entities = quizzes.map {
                SirenEntity(
                    clazz = listOf("quizzes"),
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Quiz.Id.make(it.id).toString(),
                    properties = it,
                    links = listOf(
                        SirenLink(
                            rel = listOf("self", "update_status"),
                            title = "Start",
                            type = SirenSupportedMethods.POST.toString(),
                            href = Uris.API.Web.V1_0.Auth.Quiz.Id.UpdateStatus.make(it.id).toString())
                    )
                )
            }
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.History.ENDPOINT)
    fun getHistory(request: HttpServletRequest, @RequestParam page: Int?): ResponseEntity<Any> {
        val idx = page ?: 0
        val history = service.getHistory(scope.getUser().userName, idx)
        val host = getBaseUrlHostFromRequest(request)
        val total = service.historyDocumentCount()
        val lastPage = calculateLastPage(total)
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.History.url(host, 0)))
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.History.url(host, lastPage)))
        if(idx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx + 1)))
        }
        if(idx > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx - 1)))
        }
        val body = SirenModel(
            clazz = listOf("List", "History"),
            properties = ListInfo(size = history.size, total = total.toInt()),
            entities = history.map { SirenEntity(
                clazz = listOf("History"),
                rel = listOf("item"),
                properties = HistoryOutputModel(it),
                fields = listOf(SirenField(name = "id", value = it.id))
            ) },
            links = links
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }


    @GetMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun getAllTemplatesFromUser(request: HttpServletRequest, @RequestParam pageNumber: Int?): ResponseEntity<Any> {
        val page = pageNumber?: 0
        val list = service.getAllTemplates(scope.getUser().userName, page)
        val host = getBaseUrlHostFromRequest(request)
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.Template.url(host, 0)))
        val total = service.templatesDocumentsCount()
        val lastPage = calculateLastPage(total)
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.Template.url(host, lastPage)))
        if(page < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page + 1)))
        }
        if(page > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page - 1)))
        }
        val body = SirenModel(
            clazz = listOf("ListTemplate"),
            properties = ListInfo(size = list.size, total = total.toInt()),
            entities = list.map {
                SirenEntity(
                    clazz = listOf("Template"),
                    rel = listOf("self"),
                    properties = TemplateOutputModel(it)
                )
            },
            links = links
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun createTemplate(request: HttpServletRequest, @RequestBody input: CreateTemplateInputModel): ResponseEntity<Any> {
        val doc = service.createTemplate(scope.getUser().userName, input)
        val body = SirenModel(
            clazz = listOf("CreateTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template successfully created.",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(getBaseUrlHostFromRequest(request), doc.id)
                )
            )
        )
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Template.Id.make(doc.id)).contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun getTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val doc = service.getTemplateValidatingOwner(scope.getUser().userName, id)
        val body = SirenModel(
            clazz = listOf("Template"),
            properties = doc,
            actions = listOf(
                SirenAction(
                    name = "Delete-Template",
                    title = "Delete",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(getBaseUrlHostFromRequest(request), id)
                )
            )
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @DeleteMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun deleteTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        service.deleteTemplate(scope.getUser().userName, id)
        val body = SirenModel(
            clazz = listOf("DeleteTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template was deleted successfully"
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.User.CheckUser.ENDPOINT)
    fun checkUserLoginStatus(request: HttpServletRequest): ResponseEntity<Any> {
        val cookie = request.cookies.find { it.name == "Authorization" }!!
        val doc = authService.checkUserLoginStatus(scope.getUser().userName, scope.getUser().loginToken!!)
        val body = SirenModel(
            clazz = listOf("Login"),
            //properties = Acknowledge.TRUE,
            properties = RequestLoginOutputModel(
                userName = doc.userName,
                displayName = doc.displayName,
            ),
            title = "Welcome ${doc.userName}"
        )
        return ResponseEntity.ok().body(body)
    }


}