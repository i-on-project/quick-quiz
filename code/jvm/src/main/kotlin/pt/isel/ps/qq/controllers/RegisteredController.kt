package pt.isel.ps.qq.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris

@RestController
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class RegisteredController(
    private val service: SessionService,
    private val scope: UserInfoScope
) {

    @PostMapping(Uris.API.Web.V1_0.Auth.Session.ENDPOINT)
    fun createSession(@RequestBody session: SessionInputModel): ResponseEntity<SirenJson> {
        val doc = service.createSession(scope.getUser().userName, session)
        val body = SirenJson(
            clazz = listOf("CreateSession"),
            properties = doc, //TODO null
            entities = listOf(SirenEntity.userSirenEntity(doc.owner)),
            links = listOf(SirenLink.self(Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id).toString()))
        )
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id)).body(body)
    }

    @DeleteMapping(Uris.API.Web.V1_0.Auth.Session.Id.CONTROLLER_ENDPOINT)
    fun deleteSession(@PathVariable id: String): ResponseEntity<SirenJson> {
        service.deleteSession(scope.getUser().userName, id)
        val body = SirenJson(
            clazz = listOf("DeleteSession"),
            entities = listOf(SirenEntity.userSirenEntity(scope.getUser().userName))
        )
        return ResponseEntity.ok().body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Session.Id.CONTROLLER_ENDPOINT)
    fun getSessionById(@PathVariable id: String): ResponseEntity<SirenJson> {
        val doc = service.getSessionValidatingTheOwner(scope.getUser().userName, id)
        val body = SirenJson(
            clazz = listOf("Session"),
            properties = doc,
            entities = listOf(SirenEntity(
                clazz = listOf("Quiz"),
                rel = listOf("self"),
                href = "to define"
            ),SirenEntity.userSirenEntity(doc.owner)),
            actions = listOf(
                SirenAction(
                    name = "Delete-Session",
                    title = "Delete",
                    method = "DELETE",
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(doc.id).toString(),
                ),
                SirenAction(

                )
            )
        )
        return ResponseEntity.ok().body(body)
    }

    @PutMapping("begin_quiz/{id}")
    fun startSession(@PathVariable id: String) {
        service.makeSessionLive(scope.getUser().userName, id)
    }

    @PutMapping("release_question/{id}")
    fun sendQuiz(@PathVariable id: String) {}

    /*@PostMapping("edit_session/{id}")
    fun editSession(@PathVariable id: String, @RequestBody session: ) {
        service.editSession(scope.getUser().userName, id)
    }*/

    @PostMapping("session_quiz/{id}/quiz")
    fun addQuizToSession(@PathVariable id: String, @RequestBody input: EditSessionQuizzesInputModel) {
        service.addQuizToSession(scope.getUser().userName, id, input)
    }
}