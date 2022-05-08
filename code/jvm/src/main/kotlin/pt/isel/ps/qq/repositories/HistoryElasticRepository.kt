package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.HistoryDoc
import org.springframework.stereotype.Repository

@Repository
interface HistoryElasticRepository: ElasticsearchRepository<HistoryDoc, String> {}