package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.elasticdocs.TemplateDoc

@Repository
interface TemplateElasticRepository: ElasticsearchRepository<TemplateDoc, String> {
    fun findTemplateDocsByOwner(owner: String, pageable: Pageable): List<TemplateDoc>
}