package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.data.elasticdocs.*
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.repositories.*
import pt.isel.ps.qq.repositories.customelastic.CustomRequestUpdateQuizAction
import pt.isel.ps.qq.utils.UniqueCodeGenerator
import java.util.*

@Service
class DataService(
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val sessionRepo: SessionElasticRepository,
    private val answerRepo: AnswersElasticRepository,
    private val quizRepo: QuizElasticRepository,
    private val historyRepo: HistoryElasticRepository,
    private val templateRepo: TemplateElasticRepository
) {



    fun joinSession(input: JoinSessionInputModel): AnswersDoc {
        sessionRepo.updateNumberOfParticipants(input.sessionCode)
        val session = sessionRepo.findSessionDocByGuestCode(input.sessionCode)
            ?: throw Exception("There was no session with that guest code")
        if(session.status != QqStatus.STARTED) throw Exception("Can join only in started sessions")
        val guestUuid = UUID.randomUUID().toString()
        val guestSession = AnswersDoc(id = guestUuid, sessionId = session.id)
        answerRepo.save(guestSession)
        return guestSession
    }

    fun giveAnswer(input: GiveAnswerInputModel): AnswersDoc {
        answerRepo.updateAnswerList(input)
        val opt = answerRepo.findById(input.guestId)
        if(opt.isEmpty) throw Exception("Invalid guest code... this guest may not be in the session")
        return opt.get()
    }

    fun getQuizNotAuthenticated(input: GetQuizInputModel) {

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
    fun sessionDocumentsCount(): Long {
        return sessionRepo.count()
    }

    fun templatesDocumentsCount(): Long {
        return templateRepo.count()
    }

    fun historyDocumentCount(): Long {
        return historyRepo.count()
    }

    fun getTemplateValidatingOwner(owner: String, id: String): TemplateDoc {
        val opt = templateRepo.findById(id)
        if(opt.isEmpty) throw TemplateNotFoundException()
        val doc = opt.get()
        if(doc.owner != owner) throw TemplateAuthorizationException()
        return doc
    }

    fun createSession(owner: String, input: SessionInputModel): SessionDoc {

        if(input.templateId != null) {
            val template = getTemplateValidatingOwner(owner, input.templateId)
            val sessionID = UUID.randomUUID().toString()
            val aux = mutableListOf<String>()
            template.quizzes.forEach {
                val quiz = SessionQuizDoc(it, owner, sessionID)
                quizRepo.save(quiz)
                aux.add(quiz.id)
            }
            val session = SessionDoc(template, sessionID, input, aux)
            return sessionRepo.save(session)
        }

        val sessionId = UUID.randomUUID().toString()

        val session = SessionDoc(
            id = sessionId,
            name = input.name,
            owner = owner,
            limitOfParticipants = input.limitOfParticipants ?: 10,
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
    fun makeSessionLive(username: String, id: String): Int {
        //TODO: put time trigger in elastic to close after 3h
        val generated = uniqueCodeGenerator.createID()
        sessionRepo.makeSessionGoLive(id, username, generated)
        return generated
        // open a websocket
    }

    //SessionIllegalStatusOperationException
    //SessionNotFoundException
    //SessionAuthorizationException
    fun shutdownSession(owner: String, id: String): HistoryDoc {
        val session = getSessionValidatingTheOwner(owner, id)
        if(session.status != QqStatus.STARTED) throw SessionIllegalStatusOperationException(session.status, "To perform this operation the session status can only be STARTED")
        sessionRepo.shutDownSession(id, owner)
        val quizList = quizRepo.findQuizDocsBySessionId(session.id)

        val history = HistoryDoc(session, quizList, emptyList()) //TODO: put the answers
        val toReturn = historyRepo.save(history)

        sessionRepo.deleteById(session.id)
        quizList.forEach { quizRepo.deleteById(it.id) }

        // close the websocket
        return toReturn
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
        //if(doc.status != QqStatus.NOT_STARTED) throw SessionIllegalStatusOperationException(doc.status, "To perform this operation the session status can only be NOT_STARTED")
        val newDoc = SessionDoc(doc, input)
        return sessionRepo.save(newDoc)
    }

    //SessionNotFoundException
    //SessionAuthorizationException
    //SessionIllegalStatusOperationException
    //AtLeast1CorrectChoice
    //AtLeast2Choices
    fun addQuizToSession(owner: String, sessionId: String, input: AddQuizToSessionInputModel): SessionQuizDoc {
        val quiz = SessionQuizDoc(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            userOwner = owner,
            order = input.order ?: 0,
            question = input.question,
            answerType = input.questionType,
            answerChoices = input.choices?.map { MultipleChoice(it.choiceNumber ?: 0, it.choiceAnswer, it.choiceRight) },
            quizState = QqStatus.NOT_STARTED,
            numberOfAnswers = 0
        )
        val toReturn = quizRepo.save(quiz)
        try {
            sessionRepo.updateSessionQuizzes(sessionId, owner, quiz.id ,CustomRequestUpdateQuizAction.ADD)
        } catch(ex: Exception) {
            quizRepo.deleteById(quiz.id)
            throw ex
        }
        return toReturn
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    fun getQuizValidatingOwner(owner: String, id: String): SessionQuizDoc {
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
        quizRepo.deleteById(quizDoc.id)
        sessionRepo.updateSessionQuizzes(quizDoc.sessionId, owner, quizDoc.id, CustomRequestUpdateQuizAction.REMOVE)
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    //AtLeast1CorrectChoice
    //AtLeast2Choices
    //SessionNotFoundException
    //SessionAuthorizationException
    //SessionIllegalStatusOperationException
    fun editQuiz(owner: String, id: String, input: EditQuizInputModel): SessionQuizDoc {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val session = getSessionValidatingTheOwner(owner, quizDoc.sessionId)
        //if(session.status != QqStatus.NOT_STARTED) throw SessionIllegalStatusOperationException(session.status) // conflict 409
        val newQuizDoc = SessionQuizDoc(quizDoc, input)
        return quizRepo.save(newQuizDoc)
    }

    fun updateQuizStatus(owner: String, id: String, input: UpdateQuizStausInputModel): SessionQuizDoc {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val session = getSessionValidatingTheOwner(owner, quizDoc.sessionId)
        val newQuizDoc = SessionQuizDoc(quizDoc, input.quizState)
        return quizRepo.save(newQuizDoc)
    }

    fun getHistory(user: String, page: Int): List<HistoryDoc> {
        return historyRepo.findHistoryDocsByOwner(user, PageRequest.of(page, PAGE_SIZE))
    }

    fun createTemplate(owner: String, input: CreateTemplateInputModel): TemplateDoc {
        val template = TemplateDoc(owner, input)
        return templateRepo.save(template)
    }

    fun deleteTemplate(owner: String, id: String) {
        val template = getTemplateValidatingOwner(owner, id)
        templateRepo.deleteById(id)
    }

    fun getAllTemplates(owner: String, page: Int): List<TemplateDoc> {
        return templateRepo.findTemplateDocsByOwner(owner, PageRequest.of(page, PAGE_SIZE))
    }

    fun getAllSessionQuizzes(sessionid: String): List<SessionQuizDoc> {
        return quizRepo.findQuizDocsBySessionId(sessionid)
    }



}