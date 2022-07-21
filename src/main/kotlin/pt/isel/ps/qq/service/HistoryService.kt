package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.ParticipantHistoryOutputModel
import pt.isel.ps.qq.exceptions.SessionNotFoundException
import pt.isel.ps.qq.repositories.HistoryRepository
import pt.isel.ps.qq.repositories.SessionRepository
import pt.isel.ps.qq.repositories.TemplateRepository
import pt.isel.ps.qq.repositories.docs.HistoryDoc

@Service
class HistoryService(
    sessionRepo: SessionRepository,
    templateRepo: TemplateRepository,
    private val historyRepo: HistoryRepository
) : MainDataService(sessionRepo, templateRepo) {

    fun historyDocumentCount(owner: String): Long {
        return historyRepo.countHistoryDocByOwner(owner)
    }
    fun getHistory(user: String, page: Int): List<HistoryDoc> {
        return historyRepo.findHistoryDocsByOwner(user, PageRequest.of(page, PAGE_SIZE))
    }

    fun getHistory(participantId: String, sessionId: String): ParticipantHistoryOutputModel {
        val opt = historyRepo.findById(sessionId)
        if(opt.isEmpty) throw SessionNotFoundException("This session is not yet Closed")
        val doc = opt.get()
        return ParticipantHistoryOutputModel(doc, participantId)
    }
}