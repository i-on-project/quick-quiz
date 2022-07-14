package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.AddQuizToSessionInputModel
import pt.isel.ps.qq.data.EditQuizInputModel
import pt.isel.ps.qq.data.UpdateQuizStatusInputModel
import pt.isel.ps.qq.exceptions.AtLeast2Choices
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
             quizStatus = QqStatus.NOT_STARTED,

         )
         return quizRepo.save(quiz)
    }

    fun getQuizValidatingOwner(owner: String, id: String): SessionQuizDoc {
        val opt = quizRepo.findById(id)
        if (opt.isEmpty) throw QuizNotFoundException()
        val doc = opt.get()
        if (doc.userOwner != owner) throw QuizAuthorizationException() // maybe 403
        return doc
    }

    fun removeQuizFromSession(owner: String, id: String) {
        val quizDoc = getQuizValidatingOwner(owner, id)
        quizRepo.deleteById(quizDoc.id)
    }

    fun editQuiz(owner: String, id: String, input: EditQuizInputModel): SessionQuizDoc {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val session = getSessionValidatingTheOwner(owner, quizDoc.sessionId)
        val newQuizDoc = SessionQuizDoc(quizDoc, input)
        return quizRepo.save(newQuizDoc)
    }

    fun updateQuizStatus(owner: String, id: String, input: UpdateQuizStatusInputModel): SessionQuizDoc {
        val quizDoc = getQuizValidatingOwner(owner, id)
        val session = getSessionValidatingTheOwner(owner, quizDoc.sessionId)
        val newQuizDoc = SessionQuizDoc(quizDoc, input.quizState)
        return quizRepo.save(newQuizDoc)
    }

    fun getAllSessionQuizzes(sessionid: String): List<SessionQuizDoc> {
        return quizRepo.findQuizDocsBySessionId(sessionid)
    }

    fun getAllSessionAnswersQuizzes(sessionId: String): List<SessionQuizDoc> {
        return quizRepo.findSessionQuizDocsBySessionIdAndQuizStatusNot(sessionId, QqStatus.NOT_STARTED)
    }
}