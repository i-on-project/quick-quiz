package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.repositories.AnswersRepository
import pt.isel.ps.qq.repositories.docs.Answer
import pt.isel.ps.qq.repositories.docs.AnswersDoc

@Service
class AnswersService(private val answerRepo: AnswersRepository,) {

    fun giveAnswer(input: GiveAnswerInputModel): AnswersDoc {

        //TODO: Verify Quiz Status / no update if closed / verify not started to understand what happens if a quiz was moved to not_started
        val opt = answerRepo.findById(input.guestId)
        if (opt.isEmpty) throw Exception("Invalid guest code... this guest may not be in the session")
        val doc = opt.get()
        var existingIndex = doc.answers.indexOfFirst { a -> a.quizId == input.quizId }
        if (existingIndex == -1)
            doc.answers.add(Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice))
        else
            doc.answers.set(
                existingIndex,
                Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice)
            )
        answerRepo.save(doc)
        return opt.get()
    }


    fun getAnswer(answerId: String): AnswersDoc {
        return answerRepo.findById(answerId).get() //TODO: add checks here
    }
}