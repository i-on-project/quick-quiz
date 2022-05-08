package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.customelastic.SessionCustomRequests
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionDoc

@Repository
interface SessionElasticRepository: ElasticsearchRepository<SessionDoc, String>, SessionCustomRequests {
  fun findSessionDocsByOwnerAndStatus(owner: String, status: QqStatus): List<SessionDoc>
  fun findSessionDocByGuestCode(guestCode: Int): SessionDoc?
  fun findSessionDocByGuestCodeAndStatusNot(guestCode: Int, status: QqStatus): SessionDoc?
  fun findSessionDocsByOwnerOrderById(owner: String, page: Pageable): List<SessionDoc>
  fun findSessionDocsByGuestCodeAndStatus(guestCode: Int, status: QqStatus): List<SessionDoc>
}
