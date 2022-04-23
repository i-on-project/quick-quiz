package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.repositories.customelastic.SessionCustomElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.QqStatus
import pt.isel.ps.qq.data.elasticdocs.SessionDoc


@Repository
interface SessionElasticRepository: ElasticsearchRepository<SessionDoc, String>, SessionCustomElasticsearchRepository {
  fun findSessionDocsByOwnerAndStatus(owner: String, status: QqStatus): List<SessionDoc>
  fun findSessionDocByGuestCode(guestCode: Int): SessionDoc

}
