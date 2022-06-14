package pt.isel.ps.qq

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
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.repositories.UserElasticRepository
import pt.isel.ps.qq.utils.Uris
import java.net.URI
import java.util.*
import javax.mail.Session


@Configuration
class MvcConfig(
    private val elasticRepository: UserElasticRepository,
    private val scope: UserInfoScope
) : WebMvcConfigurer {

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


    @Bean
    fun cors(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowCredentials(true)
                    .allowedHeaders(
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "token",
                        "Content-Type",
                        "X-Requested-With",
                        "accept,Origin"
                    )
                    .allowedMethods("*")
                    .allowedOriginPatterns("*")
            }
        }
    }
}


@Configuration
@EnableElasticsearchRepositories
class ElasticSearchConfig : AbstractElasticsearchConfiguration() {
    /*    @Bean
        @Primary
        fun elasticsearchTemplate(): ElasticsearchOperations? {
            return ElasticsearchRestTemplate(elasticsearchClient())
        }*/
    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {

        val connString =
            "https://Q2doMkxXDr:JkguAbiLFVaNpyUt@dogwood-417900457.eu-west-1.bonsaisearch.net:443" //todo: environment variable
        val connUri: URI = URI.create(connString)
        val auth: List<String> = connUri.userInfo.split(":")

        val clientConfiguration = ClientConfiguration
            .builder()
            .connectedTo("localhost:9200")
            .build()
        return RestClients.create(clientConfiguration).rest()

/*        val cp: CredentialsProvider = BasicCredentialsProvider()
        cp.setCredentials(AuthScope.ANY, UsernamePasswordCredentials(auth[0], auth[1]))

        val rhlc = RestHighLevelClient(
            RestClient.builder(HttpHost(connUri.host, connUri.port, connUri.scheme))
                .setHttpClientConfigCallback(
                    RestClientBuilder.HttpClientConfigCallback { httpAsyncClientBuilder: HttpAsyncClientBuilder ->
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(cp)
                            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy())
                    })
        )
        return rhlc*/
    }


}

