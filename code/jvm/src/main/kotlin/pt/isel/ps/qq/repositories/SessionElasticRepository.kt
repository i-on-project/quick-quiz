package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.customelastic.SessionCustomElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionDoc

@Repository
interface SessionElasticRepository: ElasticsearchRepository<SessionDoc, String>, SessionCustomElasticsearchRepository, PagingAndSortingRepository<SessionDoc, String> {
  fun findSessionDocsByOwnerAndStatus(owner: String, status: QqStatus): List<SessionDoc>
  fun findSessionDocByGuestCode(guestCode: Int): SessionDoc?
  fun findSessionDocByGuestCodeAndStatusNot(guestCode: Int, status: QqStatus): SessionDoc?
  fun findSessionDocsByOwner(owner: String): List<SessionDoc>
  //fun findAllByOwnerOrderByName(owner: String, pageable: Pageable)
}
