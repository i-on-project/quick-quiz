package pt.isel.ps.qq.repositories.customelastic

interface QuizCustomElasticsearchRepository {

}

class QuizCustomElasticsearchRepositoryImpl(
    private val elasticCustom: CustomElasticRequests
) : QuizCustomElasticsearchRepository