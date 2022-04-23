package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.elasticdocs.UserDoc

@Repository
interface UserElasticRepository: ElasticsearchRepository<UserDoc, String>




