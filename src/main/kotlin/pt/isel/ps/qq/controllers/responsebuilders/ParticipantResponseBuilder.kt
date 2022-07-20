package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.ListInfo
import pt.isel.ps.qq.data.SirenEntity
import pt.isel.ps.qq.data.SirenModel
import pt.isel.ps.qq.repositories.docs.ParticipantDoc
import pt.isel.ps.qq.repositories.docs.SessionQuizDoc
import pt.isel.ps.qq.utils.Uris

@Component
class ParticipantResponseBuilder {

    fun buildJoinSessionResponse(id: String): SirenModel { //TODO: New
        return SirenModel(
            clazz = listOf("participant", "id"),
            properties = mapOf("participantId" to  id )
        )
    }

    fun buildGetAllQuizzesResponse(quizzes: List<SessionQuizDoc>): SirenModel { //TODO: New
        return SirenModel(
            clazz = listOf("list", "quiz"),
            properties = ListInfo(size = quizzes.size, total = quizzes.size), //TODO: Output model required
            entities = quizzes.map {
                SirenEntity(
                    clazz = listOf("quiz"),
                    rel = listOf("item"),
                    properties = it
                )
            }
        )
    }

    fun buildGetParticipantResponse(participant: ParticipantDoc): SirenModel { //TODO: New
        return SirenModel(
            clazz = listOf("participant"),
            properties = participant,
            entities = listOf(
                SirenEntity(
                    clazz = listOf("session"),
                    rel = listOf("related"),
                    href = Uris.API.Web.V1_0.NonAuth.PATH.toString() //TODO: Output model required
                )
            )
        )
    }

    fun buildCheckInSessionResponse(participantId: String): SirenModel {
        return SirenModel(
            clazz = listOf("check", "participant"),
            properties = mapOf("participantId" to participantId)
        )
    }
}