package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.repositories.docs.ParticipantDoc
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.SessionDoc
import pt.isel.ps.qq.utils.Uris

@Component
class SessionsResponseBuilder {

    fun allSessionsResponse(baseUrl: String, totalSessions: Int, lastPage: Int, pageIdx: Int, sessions: List<SessionDoc>): SirenModel {
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.Session.url(baseUrl, 0)))
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.Session.url(baseUrl, lastPage)))
        if (pageIdx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Session.make(pageIdx + 1).toString()))
        }
        if (pageIdx > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.Session.make(pageIdx - 1).toString()))
        }
        val actionList: MutableList<SirenAction> = mutableListOf(
            SirenAction(
                name = "Create-Session",
                title = "Create",
                method = SirenSupportedMethods.POST,
                href = Uris.API.Web.V1_0.Auth.Session.PATH
            )
        )

        return SirenModel(
            clazz = listOf("ListSessionSummary"),
            properties = ListInfo(size = sessions.size, total = totalSessions),
            entities = sessions.map {
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
    }
    fun createSessionResponse(session: SessionDoc): SirenModel{
        return SirenModel(
            clazz = listOf("CreateSession"),
            properties = session,
            title = "Session successfully created.",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Session.Id.make(session.id).toString()
                )
            )
        )
    }
    fun deleteSessionResponse(): SirenModel{
        return SirenModel(
            clazz = listOf("DeleteSession"), properties = Acknowledge.TRUE, title = "Session successfully deleted."
        )
    }
    fun getSessionResponse(id: String, session: SessionDoc ): SirenModel{
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
        )
        when (session.status) {
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
        return SirenModel(
            clazz = listOf("Session"),
            properties = SessionOutputModel(session), // todo: entities = listOf(SirenEntity.quizzesSirenEntity(doc.id),SirenEntity.userSirenEntity(doc.owner)),
            actions = actionList,
            links = listOf(
                SirenLink(
                    listOf("List", "Quiz"),
                    "Quizzes",
                    listOf("related"),
                    Uris.API.Web.V1_0.Auth.Quiz.SessionId.make(session.id).toString()
                ),
                SirenLink(
                    listOf("List", "Answers"),
                    "Answers",
                    listOf("related"),
                    Uris.API.Web.V1_0.Auth.Session.Id.Answers.make(session.id).toString()
                )
            )
        )
    }
    fun editSessionResponse(baseUrl: String, sessionId: String): SirenModel{
        return SirenModel(
            clazz = listOf("EditSession"), properties = Acknowledge.TRUE, links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Session.Id.url(baseUrl, sessionId)
                )
            ), title = "Session successfully updated."
        )
    }
    fun startSessionResponse(code: Int): SirenModel{
        return SirenModel(
            clazz = listOf("LiveSession"),
            properties = LiveSession(code.toString().padStart(10, '0')),
            title = "Session went live successfully."
        )
    }
    fun closeSessionResponse(): SirenModel{
        return SirenModel(
            clazz = listOf("LiveSession"), properties = Acknowledge.TRUE, title = "Session was closed successfully."
        )
    }
    fun getAllAnswersResponse(participantsAndAnswers: List<ParticipantDoc>): SirenModel{
        return SirenModel(
            clazz = listOf("ListAnswersSummary"),
            properties = ListInfo(size = participantsAndAnswers.size, total = participantsAndAnswers.size),
            entities = participantsAndAnswers.map {
                SirenEntity(
                    clazz = listOf("AnswerSummary"),
                    rel = listOf("self"),
                    //href = Uris.API.Web.V1_0.Auth.Session.Id.make(it.id).toString(),
                    properties = AnswersOutputModel(it), //TODO

                )
            }
        )
    }
}