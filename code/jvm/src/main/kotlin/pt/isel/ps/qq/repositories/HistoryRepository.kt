package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.docs.HistoryDoc

@Repository
interface HistoryRepository: MongoRepository<HistoryDoc, String> {
    fun findHistoryDocsByOwner(owner: String, pageable: Pageable): List<HistoryDoc>
}
