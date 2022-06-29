package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.SessionDoc


@Repository
interface SessionRepository: MongoRepository<SessionDoc, String> {
  fun findSessionDocByIdAndOwner(id: String, owner: String): SessionDoc
  fun findSessionDocsByOwnerAndStatus(owner: String, status: QqStatus): List<SessionDoc>
  fun findSessionDocByGuestCode(guestCode: Int): SessionDoc?
  fun findSessionDocByGuestCodeAndStatusNot(guestCode: Int, status: QqStatus): SessionDoc?
  fun findSessionDocsByOwnerOrderById(owner: String, page: Pageable): List<SessionDoc>
  fun findSessionDocsByGuestCodeAndStatus(guestCode: Int, status: QqStatus): List<SessionDoc>
  fun countSessionDocByOwnerAndStatus(owner: String, status: QqStatus): Long
  fun countSessionDocByIdAndStatus(id: String, status: QqStatus): Long


}
