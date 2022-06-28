package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.AddQuizToSessionInputModel
import pt.isel.ps.qq.data.EditQuizInputModel
import pt.isel.ps.qq.data.UpdateQuizStausInputModel
import pt.isel.ps.qq.exceptions.QuizAuthorizationException
import pt.isel.ps.qq.exceptions.QuizNotFoundException
import pt.isel.ps.qq.repositories.QuizRepository
import pt.isel.ps.qq.repositories.SessionRepository
import pt.isel.ps.qq.repositories.TemplateRepository
import pt.isel.ps.qq.repositories.docs.MultipleChoice
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.SessionQuizDoc
import java.util.*

@Service
class QuizService(
    sessionRepo: SessionRepository,
    templateRepo: TemplateRepository,
    private val quizRepo: QuizRepository,
) : MainDataService(sessionRepo, templateRepo) {

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
            answerChoices = input.choices?.map {
                MultipleChoice(
                    it.choiceNumber ?: 0,
                    it.choiceAnswer,
                    it.choiceRight
                )
            },
            quizState = QqStatus.NOT_STARTED,
            numberOfAnswers = 0
        )


/* try {
     sessionRepo.updateSessionQuizzes(sessionId, owner, quiz.id ,CustomRequestUpdateQuizAction.ADD)
 } catch(ex: Exception) {
     quizRepo.deleteById(quiz.id)
     throw ex
 }*/
        return quizRepo.save(quiz)
    }

    //QuizNotFoundException
//QuizAuthorizationException
    fun getQuizValidatingOwner(owner: String, id: String): SessionQuizDoc {
        val opt = quizRepo.findById(id)
        if (opt.isEmpty) throw QuizNotFoundException()
        val doc = opt.get()
        if (doc.userOwner != owner) throw QuizAuthorizationException() // maybe 403
        return doc
    }

    //QuizNotFoundException
    //QuizAuthorizationException
    //SessionIllegalStatusOperationException
    fun removeQuizFromSession(owner: String, id: String) {
        val quizDoc = getQuizValidatingOwner(owner, id)
        quizRepo.deleteById(quizDoc.id)
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

    fun getAllSessionQuizzes(sessionid: String): List<SessionQuizDoc> {
        return quizRepo.findQuizDocsBySessionId(sessionid)
    }

    fun getAllSessionAnswersQuizzes(sessionId: String): List<SessionQuizDoc> {
        return quizRepo.findSessionQuizDocsBySessionIdAndQuizStateNot(sessionId, QqStatus.NOT_STARTED)
    }
}