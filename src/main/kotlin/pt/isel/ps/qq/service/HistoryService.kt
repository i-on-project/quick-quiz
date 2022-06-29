package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.repositories.HistoryRepository
import pt.isel.ps.qq.repositories.SessionRepository
import pt.isel.ps.qq.repositories.TemplateRepository
import pt.isel.ps.qq.repositories.docs.HistoryDoc

@Service
class HistoryService(sessionRepo: SessionRepository,
                     templateRepo: TemplateRepository,
                     private val historyRepo: HistoryRepository,)
    : MainDataService(sessionRepo, templateRepo) {



    fun historyDocumentCount(): Long {
        return historyRepo.count()
    }
    fun getHistory(user: String, page: Int): List<HistoryDoc> {
        return historyRepo.findHistoryDocsByOwner(user, PageRequest.of(page, PAGE_SIZE))
    }


}