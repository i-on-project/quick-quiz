package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.data.elasticdocs.*
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.repositories.GuestSessionElasticRepository
import pt.isel.ps.qq.repositories.HistoryElasticRepository
import pt.isel.ps.qq.repositories.QuizElasticRepository
import pt.isel.ps.qq.repositories.SessionElasticRepository
import java.util.*

@Service
class DataService(
    private val random: Random,
    private val sessionRepo: SessionElasticRepository,
    private val guestSessionRepo: GuestSessionElasticRepository,
    private val quizRepo: QuizElasticRepository,
    private val historyRepo: HistoryElasticRepository
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


    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    companion object {
        const val PAGE_SIZE = 10
    }

    /**
     * Returns the number of documents present on the data repository 'sessions'.
     * @return Returns the number of documents present on the data repository 'sessions'
     */
    fun documentsCount(): Long {
        return sessionRepo.count()
    }

    fun createSession(owner: String, input: SessionInputModel): SessionDoc {

        val sessionId = UUID.randomUUID().toString()

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            owner = owner,
            limitOfParticipants = input.limitOfParticipants,
            status = QqStatus.NOT_STARTED,
            numberOfParticipants = 0
        )
        return sessionRepo.save(session)
    }

    //SessionAuthorizationException
    //SessionNotFoundException
    fun deleteSession(user: String, id: String) {
        getSessionValidatingTheOwner(user, id)
        sessionRepo.deleteById(id)
    }

    fun getAllSessions(user: String, page: Int): List<SessionDoc> {
        return sessionRepo.findSessionDocsByOwnerOrderById(user, PageRequest.of(page, PAGE_SIZE))
    }

    //SessionIllegalStatusOperationException
    //ImpossibleGenerationException
    //SessionNotFoundException
    //SessionAuthorizationException
    fun makeSessionLive(username: String, id: String): SessionDoc {
        val session = getSessionValidatingTheOwner(username, id)

        //TODO: Only 1 session can be started

        if(session.status != QqStatus.NOT_STARTED) throw SessionIllegalStatusOperationException(session.status, "To perform this operation the session status can only be NOT_STARTED")

        val counter = HashSet<Int>()
        while(counter.count() != 1000000) {
            val generated = random.nextInt(1000000)
            if(counter.contains(generated)) continue
            else counter.add(generated)
            val maybe = sessionRepo.findSessionDocsByGuestCodeAndStatus(generated, QqStatus.STARTED)
            if(maybe.isEmpty()) sessionRepo.makeSessionGoLive(id, generated)
            else continue
            val list = sessionRepo.findSessionDocsByGuestCodeAndStatus(generated, QqStatus.STARTED)
            if(list.count() == 1) return list[0]
        }

        throw ImpossibleGenerationException()
        // open a websocket
    }

    //SessionIllegalStatusOperationException
    //SessionNotFoundException
    //SessionAuthorizationException
    fun shutdownSession(owner: String, id: String): HistoryDoc {

        val session = getSessionValidatingTheOwner(owner, id)

        if(session.status != QqStatus.STARTED) throw SessionIllegalStatusOperationException(session.status, "To perform this operation the session status can only be STARTED")

        sessionRepo.updateStatus(id, QqStatus.CLOSED)

        val history = HistoryDoc(session, quizRepo.findQuizDocsBySessionId(session.id)) //Todo: get quizzes associated with this session and the answers

        //TODO: fix the issue of have multiple history for the same session because of multithreading issues
        // solve with lock

        return historyRepo.save(history)

        // close the websocket


    }

    //SessionNotFoundException
    //SessionAuthorizationException
    fun getSessionValidatingTheOwner(owner: String, id: String): SessionDoc {
        val opt = sessionRepo.findById(id)
        if(opt.isEmpty) throw SessionNotFoundException()
        val doc = opt.get()
        if(doc.owner != owner) throw SessionAuthorizationException()
        return doc
    }

    //SessionNotFoundException
    //SessionAuthorizationException
    fun editSession(owner: String, id: String, input: EditSessionInputModel): SessionDoc {
        val doc = getSessionValidatingTheOwner(owner, id)
        val newDoc = SessionDoc(doc, input)
        return sessionRepo.save(newDoc)
    }

    //SessionNotFoundException
    //SessionAuthorizationException
    //SessionIllegalStatusOperationException
    //AtLeast1CorrectChoice
    //AtLeast2Choices
    fun addQuizToSession(owner: String, id: String, input: AddQuizToSessionInputModel): QuizDoc {
        val session = getSessionValidatingTheOwner(owner, id)
        if(session.status == QqStatus.CLOSED) throw SessionIllegalStatusOperationException(session.status, "To perform this operation the session status can only be CLOSED") // conflict 409

        val quiz = QuizDoc(
            id = UUID.randomUUID().toString(),
            sessionId = session.id,
            userOwner = session.owner,
            order = input.order ?: 0,
            question = input.question,
            answerType = input.questionType,
            answerChoices = input.choices?.map { MultipleChoice(it.choiceNumber ?: 0, it.choice, it.choiceRight) },
            quizState = QqStatus.NOT_STARTED,
            numberOfAnswers = 0
        )
        sessionRepo.updateQuizzes(id, quiz.id)
        return quizRepo.save(quiz)
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    fun getQuizValidatingOwner(owner: String, id: String): QuizDoc {
        val opt = quizRepo.findById(id)
        if(opt.isEmpty) throw QuizNotFoundException()
        val doc = opt.get()
        if(doc.userOwner != owner) throw QuizAuthorizationException() // maybe 403
        return doc
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    //SessionIllegalStatusOperationException
    fun removeQuizFromSession(owner: String, id: String) {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val session = getSessionValidatingTheOwner(owner, quizDoc.sessionId)
        if(session.status != QqStatus.NOT_STARTED) throw SessionIllegalStatusOperationException(session.status) // conflict 409
        session.quizzes.find { it == quizDoc.id } ?: throw QuizNotFoundException("The session doesn't have this quiz", session.id) // 400 bad request
        quizRepo.deleteById(quizDoc.id)
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    //AtLeast1CorrectChoice
    //AtLeast2Choices
    fun editQuiz(owner: String, id: String, input: EditQuizInputModel): QuizDoc {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val newQuizDoc = QuizDoc(quizDoc, input)
        return quizRepo.save(newQuizDoc)
    }
}