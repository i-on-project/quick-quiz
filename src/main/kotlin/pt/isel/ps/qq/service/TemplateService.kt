package pt.isel.ps.qq.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.CreateTemplateInputModel
import pt.isel.ps.qq.repositories.HistoryRepository
import pt.isel.ps.qq.repositories.SessionRepository
import pt.isel.ps.qq.repositories.TemplateRepository
import pt.isel.ps.qq.repositories.docs.TemplateDoc

@Service
class TemplateService(
    sessionRepo: SessionRepository,
    private val templateRepo: TemplateRepository,
) : MainDataService(sessionRepo, templateRepo) {

    fun templatesDocumentsCount(): Long {
        return templateRepo.count()
    }

    fun createTemplate(owner: String, input: CreateTemplateInputModel): TemplateDoc {
        val template = TemplateDoc(owner, input)
        return templateRepo.save(template)
    }

    fun deleteTemplate(owner: String, id: String) {
        val template = getTemplateValidatingOwner(owner, id)
        templateRepo.deleteById(id)
    }

    fun getAllTemplates(owner: String, page: Int): List<TemplateDoc> {
        return templateRepo.findTemplateDocsByOwner(owner, PageRequest.of(page, PAGE_SIZE))
    }


}