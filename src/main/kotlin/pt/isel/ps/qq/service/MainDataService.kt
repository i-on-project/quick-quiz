package pt.isel.ps.qq.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.ps.qq.exceptions.SessionAuthorizationException
import pt.isel.ps.qq.exceptions.SessionNotFoundException
import pt.isel.ps.qq.exceptions.TemplateAuthorizationException
import pt.isel.ps.qq.exceptions.TemplateNotFoundException
import pt.isel.ps.qq.filters.LogFilter
import pt.isel.ps.qq.repositories.SessionRepository
import pt.isel.ps.qq.repositories.TemplateRepository
import pt.isel.ps.qq.repositories.docs.SessionDoc
import pt.isel.ps.qq.repositories.docs.TemplateDoc

@Service
class MainDataService(private val sessionRepo: SessionRepository,
                      private val templateRepo: TemplateRepository) {

    companion object {
        const val PAGE_SIZE = 10
        val logger: Logger = LoggerFactory.getLogger(LogFilter::class.java)
    }

    fun getTemplateValidatingOwner(owner: String, id: String): TemplateDoc {
        val opt = templateRepo.findById(id)
        if (opt.isEmpty) throw TemplateNotFoundException()
        val doc = opt.get()
        if (doc.owner != owner) throw TemplateAuthorizationException()
        return doc
    }

    fun getSessionValidatingTheOwner(owner: String, id: String): SessionDoc {
        val opt = sessionRepo.findById(id)
        if (opt.isEmpty) throw SessionNotFoundException()
        val doc = opt.get()
        if (doc.owner != owner) throw SessionAuthorizationException()
        return doc
    }

}