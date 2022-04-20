package pt.isel.ps.qq.database

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.database.elasticdocs.UserDoc

@Repository
interface UserElasticRepository: ElasticsearchRepository<UserDoc, String>




