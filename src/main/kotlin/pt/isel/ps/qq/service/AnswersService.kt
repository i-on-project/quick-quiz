package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.GiveAnswerInputModel
import pt.isel.ps.qq.exceptions.GuestSessionNotFoundException
import pt.isel.ps.qq.repositories.ParticipantRepository
import pt.isel.ps.qq.repositories.docs.Answer
import pt.isel.ps.qq.repositories.docs.ParticipantDoc

@Service
class AnswersService(private val answerRepo: ParticipantRepository,) {

    fun giveAnswer(input: GiveAnswerInputModel): ParticipantDoc {

        //TODO: Verify Quiz Status / no update if closed / verify not started to understand what happens if a quiz was moved to not_started
        val opt = answerRepo.findById(input.guestId)
        if (opt.isEmpty) throw GuestSessionNotFoundException("Invalid guest code... this guest may not be in the session")
        val doc = opt.get()
        val existingIndex = doc.answers.indexOfFirst { a -> a.quizId == input.quizId }
        if (existingIndex == -1)
            doc.answers.add(Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice))
        else doc.answers[existingIndex] = Answer(quizId = input.quizId, answer = input.answer, answerNumber = input.answerChoice)
        answerRepo.save(doc)
        return opt.get()
    }


    fun getParticipant(answerId: String): ParticipantDoc {
        val participantDoc = answerRepo.findById(answerId) //TODO: add checks here
        if(participantDoc.isEmpty) throw GuestSessionNotFoundException()
        return participantDoc.get()
    }
}