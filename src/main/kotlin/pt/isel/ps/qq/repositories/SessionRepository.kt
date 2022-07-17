package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.SessionDoc


@Repository
interface SessionRepository: MongoRepository<SessionDoc, String> {

  fun findSessionDocByGuestCodeAndStatus(guestCode: Int, status: QqStatus): SessionDoc?
  fun findSessionDocsByOwnerOrderById(owner: String, page: Pageable): List<SessionDoc>
  fun countSessionDocByOwnerAndStatus(owner: String, status: QqStatus): Long
  fun countSessionDocByOwner(owner: String): Long
  fun countSessionDocByIdAndStatus(id: String, status: QqStatus): Long


}
