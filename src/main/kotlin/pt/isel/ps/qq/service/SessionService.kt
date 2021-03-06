package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.EditSessionInputModel
import pt.isel.ps.qq.data.JoinSessionInputModel
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.repositories.*
import pt.isel.ps.qq.repositories.docs.*
import java.util.*

@Service
class SessionService(
    private val sessionRepo: SessionRepository,
    private val participantRepo: ParticipantRepository,
    private val quizRepo: QuizRepository,
    templateRepo: TemplateRepository,
    private val historyRepo: HistoryRepository,
) : MainDataService(sessionRepo, templateRepo) {


    fun joinSession(input: JoinSessionInputModel): ParticipantDoc {
        //sessionRepo.updateNumberOfParticipants(input.sessionCode)
        val session = sessionRepo.findSessionDocByGuestCode(input.sessionCode)
            ?: throw SessionNotFoundException("There was no session with that guest code")
        if (session.status != QqStatus.STARTED) throw SessionIllegalStatusOperationException(
            session.status,
            "Can join only in started sessions"
        )
        val guestUuid = UUID.randomUUID().toString()
        val guestSession = ParticipantDoc(id = guestUuid, sessionId = session.id)
        participantRepo.save(guestSession)
        return guestSession
    }


    fun editSession(owner: String, id: String, input: EditSessionInputModel): SessionDoc {
        val doc = getSessionValidatingTheOwner(owner, id)
        if (doc.status != QqStatus.NOT_STARTED) throw SessionIllegalStatusOperationException(
            doc.status,
            "To perform this operation the session status can only be NOT_STARTED"
        )
        val newDoc = SessionDoc(doc, input)
        return sessionRepo.save(newDoc)
    }

    fun getAllAnswersForSession(owner: String, id: String): List<ParticipantDoc> {
        getSessionValidatingTheOwner(owner, id)
        return participantRepo.findAnswersDocsBySessionId(id)
    }

    fun createSession(owner: String, input: SessionInputModel): SessionDoc {

        if (input.templateId != null) {
            val template = getTemplateValidatingOwner(owner, input.templateId)
            val sessionID = UUID.randomUUID().toString()
            val aux = mutableListOf<String>()
            template.quizzes.forEach {
                val quiz = SessionQuizDoc(it, owner, sessionID)
                quizRepo.save(quiz)
                aux.add(quiz.id)
            }
            val session = SessionDoc(template, sessionID, input)

            return sessionRepo.save(session)
        }

        val sessionId = UUID.randomUUID().toString()

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            description = input.description,
            owner = owner,
            limitOfParticipants = input.limitOfParticipants ?: 10,
            geolocation = input.geolocation,
            radius = input.radius,
            status = QqStatus.NOT_STARTED,
            tags = input.tags.toMutableList()
        )
        return sessionRepo.save(session)
    }

    fun checkSessionIsLive(sessionId: String): Boolean {
        return sessionRepo.countSessionDocByIdAndStatus(sessionId, QqStatus.STARTED) > 0
    }


    fun makeSessionLive(username: String, id: String): Int {

        var generated = id.hashCode()
        generated = if (generated < 0) generated * -1 else generated

        if (sessionRepo.countSessionDocByOwnerAndStatus(
                username,
                QqStatus.STARTED
            ) > 0
        ) throw LiveSessionAlreadyExists()

        val session = getSessionValidatingTheOwner(username, id)

        updateSessionStatus(session, QqStatus.STARTED, generated)

        return generated

    }

    fun shutdownSession(owner: String, id: String): HistoryDoc {
        val session = getSessionValidatingTheOwner(owner, id)
        if (session.status != QqStatus.STARTED) throw SessionIllegalStatusOperationException(
            session.status,
            "To perform this operation the session status can only be STARTED"
        )
        updateSessionStatus(session, QqStatus.CLOSED, session.guestCode)
        val quizList = quizRepo.findQuizDocsBySessionId(session.id)
        val participants = participantRepo.findAnswersDocsBySessionId(session.id)
        val history = HistoryDoc(session, quizList, participants)
        val toReturn = historyRepo.save(history)

        sessionRepo.deleteById(session.id)
        quizList.forEach { quizRepo.deleteById(it.id) }
        participants.forEach { participantRepo.deleteById(it.id) }
        return toReturn
    }

    private fun updateSessionStatus(session: SessionDoc, status: QqStatus, sessionCode: Int?) {
        val updatedSession = SessionDoc(session, status, sessionCode)
        sessionRepo.save(updatedSession)
    }

    fun deleteSession(user: String, id: String) {
        getSessionValidatingTheOwner(user, id)
        sessionRepo.deleteById(id)
        val quizList = quizRepo.findQuizDocsBySessionId(id)
        quizList.forEach { quizRepo.deleteById(it.id) }
    }

    fun getAllSessions(user: String, page: Int): List<SessionDoc> {
        return sessionRepo.findSessionDocsByOwnerOrderById(user, PageRequest.of(page, PAGE_SIZE))
    }

    /**
     * Returns the number of documents present on the data repository 'sessions' for a given user.
     * @return Returns the number of documents present on the data repository 'sessions' for a given user
     */
    fun sessionDocumentsCount(userName: String): Long {
        return sessionRepo.countSessionDocByOwner(userName)
    }


}