package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.ListInfo
import pt.isel.ps.qq.data.SirenEntity
import pt.isel.ps.qq.data.SirenModel
import pt.isel.ps.qq.repositories.docs.ParticipantDoc
import pt.isel.ps.qq.repositories.docs.SessionQuizDoc

@Component
class ParticipantResponseBuilder {

    fun buildGetAllQuizzesResponse(quizzes: List<SessionQuizDoc>): SirenModel {
        return SirenModel(
            clazz = listOf("Quiz"),
            properties = ListInfo(size = quizzes.size, total = quizzes.size), //TODO: Output model required
            entities = quizzes.map {
                SirenEntity(
                    clazz = listOf("quizzes"),
                    rel = listOf("self"),
                    properties = it
                )
            }
        )
    }

    fun buildGetParticipantResponse(participant: ParticipantDoc): ParticipantDoc { //TODO: build siren model
        return participant
    }
}