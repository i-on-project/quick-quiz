package pt.isel.ps.qq.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.annotation.HandleAfterCreate
import org.springframework.data.rest.core.annotation.HandleAfterDelete
import org.springframework.data.rest.core.annotation.HandleAfterSave
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import pt.isel.ps.qq.WebSocketConfiguration.Companion.MESSAGE_PREFIX
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.hateoas.server.EntityLinks
import pt.isel.ps.qq.data.elasticdocs.QuizDoc
import pt.isel.ps.qq.data.elasticdocs.SessionDoc

@Component
@RepositoryEventHandler
class EventHandler (private val websocket: SimpMessagingTemplate, private val entityLinks: EntityLinks) {

    @HandleAfterCreate
    fun newQuiz(quiz: QuizDoc) {
        websocket.convertAndSend( MESSAGE_PREFIX + "/newQuiz", getPath(quiz)
        )
    }

    @HandleAfterDelete
    fun deleteEmployee(quiz: QuizDoc) {
        websocket.convertAndSend( MESSAGE_PREFIX + "/deleteQuiz", getPath(quiz)
        )
    }

    @HandleAfterSave
    fun updatQuiz(quiz: QuizDoc) {
        websocket.convertAndSend( MESSAGE_PREFIX + "/updateQuiz", getPath(quiz)
        )
    }

    /**
     * Take an [Employee] and get the URI using Spring Data REST's [EntityLinks].
     *
     * @param employee
     */
    private fun getPath(quiz: QuizDoc): String {
        return entityLinks.linkForItemResource(
            quiz.javaClass,
            quiz.id
        ).toUri().path
    }
}