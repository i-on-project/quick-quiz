package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.elasticdocs.AnswersDoc
import pt.isel.ps.qq.repositories.customelastic.AnswerCustomRequests

@Repository
interface AnswersElasticRepository: ElasticsearchRepository<AnswersDoc, String>, AnswerCustomRequests