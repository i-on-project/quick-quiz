package pt.isel.ps.qq.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import pt.isel.ps.qq.data.elasticdocs.GuestSessionDoc
import pt.isel.ps.qq.repositories.customelastic.GuestSessionCustomElasticRepository

interface GuestSessionElasticRepository: ElasticsearchRepository<GuestSessionDoc, String>,
    GuestSessionCustomElasticRepository