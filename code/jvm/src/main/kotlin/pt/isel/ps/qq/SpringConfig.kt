package pt.isel.ps.qq

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.repositories.UserElasticRepository
import pt.isel.ps.qq.utils.Uris
import java.util.Random
import javax.mail.Session

@Configuration
class MvcConfig(
    private val elasticRepository: UserElasticRepository,
    private val scope: UserInfoScope
): WebMvcConfigurer {

    @Bean
    fun mailSession(): Session {
        val properties = System.getProperties()
        properties.setProperty("mail.smtp.host", "localhost")
        return Session.getDefaultInstance(properties)
    }

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()

    @Bean
    fun random(): Random = Random()

    @Bean
    fun userFilterRegistrationBean(): FilterRegistrationBean<UserFilter> {
        val registrationBean: FilterRegistrationBean<UserFilter> = FilterRegistrationBean<UserFilter>()
        registrationBean.filter = UserFilter(elasticRepository, scope)
        registrationBean.addUrlPatterns("${Uris.API.Web.V1_0.Auth.PATH}/*")
        return registrationBean
    }

/*    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf{ it is MappingJackson2HttpMessageConverter }

        val customJson = MappingJackson2HttpMessageConverter()
        customJson.setPrettyPrint(false)
        customJson.apply {  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY) }

        converters.add(customJson)
    }*/
}

@Configuration
@EnableElasticsearchRepositories
class ElasticSearchConfig: AbstractElasticsearchConfiguration() {

    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {
        val clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").build()
        return RestClients.create(clientConfiguration).rest()
    }
}