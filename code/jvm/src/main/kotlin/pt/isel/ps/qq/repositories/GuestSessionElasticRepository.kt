package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import pt.isel.ps.qq.repositories.elasticdocs.GuestSessionDoc

interface GuestSessionElasticRepository: ElasticsearchRepository<GuestSessionDoc, String>