package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.repositories.docs.SessionQuizDoc
import pt.isel.ps.qq.utils.Uris

@Component
class QuizzesresponseBuilder {
    fun addQuizzResponse(host: String, sessionId: String, quizId: String): SirenModel{ //TODO: Added id to property
        return SirenModel(
            clazz = listOf("Quiz"),
            properties = mapOf("id" to quizId),
            entities = listOf(
                SirenEntity(
                    rel = listOf("session"), links = listOf(
                        SirenLink(
                            rel = listOf("self"), href = Uris.API.Web.V1_0.Auth.Session.Id.url(host, sessionId)
                        )
                    )
                )
            ),
            title = "Quiz was added successfully",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Quiz.Id.url(host, quizId)
                )
            )
        )
    }

    fun getAllQuizzesForSessionResponse(quizzes: List<SessionQuizDoc>): SirenModel {
        return SirenModel(
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
                            href = Uris.API.Web.V1_0.Auth.Quiz.Id.UpdateStatus.make(it.id).toString()
                        )
                    )
                )
            }
        )
    }

    fun updateQuizStatusResponse(): SirenModel {
        return SirenModel(clazz = listOf("TODO"))
    }

    fun editQuizResponse(): SirenModel {
        return SirenModel(clazz = listOf("TODO"))
    }

    fun removeQuizResponse(): SirenModel {
        return SirenModel(
            clazz = listOf("DeleteSession"), properties = Acknowledge.TRUE, title = "Session successfully deleted."
        )
    }

    fun getQuizResponse(quiz: SessionQuizDoc): SirenModel {
        return SirenModel(
            clazz = listOf("Quiz"), //TODO: add actions update
            properties = quiz
        )
    }
}