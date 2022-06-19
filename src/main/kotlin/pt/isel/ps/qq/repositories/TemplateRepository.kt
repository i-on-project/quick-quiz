package pt.isel.ps.qq.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.docs.TemplateDoc

@Repository
interface TemplateRepository: MongoRepository<TemplateDoc, String> {
    fun findTemplateDocsByOwner(owner: String, pageable: Pageable): List<TemplateDoc>
}