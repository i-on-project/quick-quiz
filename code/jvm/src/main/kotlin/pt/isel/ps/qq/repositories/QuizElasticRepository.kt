package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.QuizDoc
import org.springframework.stereotype.Repository

@Repository
interface QuizElasticRepository: ElasticsearchRepository<QuizDoc, String>