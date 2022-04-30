package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.EditSessionQuizzesInputModel
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.data.JoinSessionInputModel
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.data.elasticdocs.*
import pt.isel.ps.qq.exceptions.OpenedSessionException
import pt.isel.ps.qq.repositories.GuestSessionElasticRepository
import pt.isel.ps.qq.repositories.QuizElasticRepository
import pt.isel.ps.qq.repositories.SessionElasticRepository
import java.util.*

@Service
class SessionService(
    private val sessionRepo: SessionElasticRepository, private val guestSessionRepo: GuestSessionElasticRepository,
    private val quizRepo: QuizElasticRepository
) {
    fun joinSession(input: JoinSessionInputModel): GuestSessionDoc {
        sessionRepo.updateNumberOfParticipants(input.sessionCode)
        val session = sessionRepo.findSessionDocByGuestCode(input.sessionCode)
            ?: throw Exception("There was no session with that guest code")
        val guestUuid = UUID.randomUUID().toString()
        val guestSession = GuestSessionDoc(id = guestUuid, sessionId = session.id)
        guestSessionRepo.save(guestSession)
        return guestSession
    }

    fun giveAnswer(input: GiveAnswerInputModel): GuestSessionDoc {
        guestSessionRepo.updateAnswerList(input)
        val opt = guestSessionRepo.findById(input.guestId)
        if(opt.isEmpty) throw Exception("Invalid guest code... this guest may not be in the session")
        return opt.get()
    }

    fun createSession(owner: String, input: SessionInputModel): SessionDoc {

        val openSessions = sessionRepo.findSessionDocsByOwnerAndStatus(owner, QqStatus.STARTED)
        if(openSessions.isNotEmpty()) throw OpenedSessionException()

        val sessionId = UUID.randomUUID().toString()
        var guestCode = sessionId.hashCode()
        var count = 0
        while(!validateUniqueGuestCode(guestCode)) {
            if(count >= 3) throw Exception("We dont know what to do here")
            guestCode *= 31
            ++count
        }
        guestCode = if(guestCode < 0) guestCode * -1 else guestCode

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            owner = owner,
            guestCode = guestCode,
            limitOfParticipants = input.limitOfParticipants,
            endDate = input.endDate,
            status = QqStatus.NOT_STARTED,
            numberOfParticipants = 0
        )
        return sessionRepo.save(session)
    }

    private fun validateUniqueGuestCode(code: Int): Boolean {
        val doc = sessionRepo.findSessionDocByGuestCodeAndStatusNot(code, QqStatus.CLOSED)
        return doc == null
    }

    fun deleteSession(user: String, id: String) {
        getSessionValidatingTheOwner(user, id)
        sessionRepo.deleteById(id)
    }

    fun getAllSessions(user: String, id: String): List<SessionDoc> {
        return sessionRepo.findSessionDocsByOwner(user)
    }


    fun makeSessionLive(username: String, id: String) {
        val session = getSessionValidatingTheOwner(username, id)

        if(session.status == QqStatus.STARTED) throw Exception("Session has already started")
        if(session.status == QqStatus.CLOSED) throw Exception("Session has already closed")

        sessionRepo.updateStatus(id, QqStatus.STARTED)

        // temos de abrir um websockect
    }

    fun getSessionValidatingTheOwner(owner: String, id: String): SessionDoc {
        val opt = sessionRepo.findById(id)
        if(opt.isEmpty) throw Exception("Not Found")
        val doc = opt.get()
        if(doc.owner != owner) throw Exception("The user $owner don´t have authority over this session")// maybe 403
        return doc
    }

    fun editSession(owner: String, id: String) {

    }

    fun addQuizToSession(owner: String, id: String, input: EditSessionQuizzesInputModel) {
        val session = getSessionValidatingTheOwner(owner, id)
        var choices: List<MultipleChoice> = emptyList()
        if(input.questionType == QuestionType.MULTIPLE_CHOICE) {
            if(input.choices == null || input.choices.count() < 2) throw Exception("400 bad request tem de ter pelo menos 2 resposta para ser de escolha multipla")
            input.choices.find { it.choiceRight } ?: throw Exception("400 bad request tem de ter pelo menos 1 resposta correta")
            choices = input.choices.map { MultipleChoice(it.choiceNumber ?: 0, it.choice, it.choiceRight) }
        }
        val quiz = QuizDoc(
           id = UUID.randomUUID().toString(),
           sessionId = id,
           userOwner = owner,
           order = input.order ?: 0,
           question = input.question,
           answerType = input.questionType,
           answerChoices = choices,
           quizState = QqStatus.NOT_STARTED,
           numberOfAnswers = 0
        )
        quizRepo.save(quiz)
    }

}