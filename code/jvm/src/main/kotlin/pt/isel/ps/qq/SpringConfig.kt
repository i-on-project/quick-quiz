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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.repositories.UserElasticRepository
import pt.isel.ps.qq.utils.Uris
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
    fun userFilterRegistrationBean(): FilterRegistrationBean<UserFilter> {
        val registrationBean: FilterRegistrationBean<UserFilter> = FilterRegistrationBean<UserFilter>()
        registrationBean.filter = UserFilter(elasticRepository, scope)
        registrationBean.addUrlPatterns("${Uris.API.Web.V1_0.Auth.PATH}/*")
        return registrationBean
    }
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

/*@Configuration
@EnableWebSecurity
class AuthorizationConfig(
    private val elasticRepository: UserElasticRepository
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().antMatchers(
            "${Uris.API.Web.V1_0.NonAuth.PATH}/*"
        ).permitAll().antMatchers("${Uris.API.Web.V1_0.Auth.PATH}/*").authenticated().and().httpBasic()
        //http.formLogin().loginPage(Uris.API.Web.V1_0.NonAuth.Logmein.PATH).permitAll().and().logout().permitAll()
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailManagerImpl(elasticRepository)
    }
}

class UserDetailManagerImpl(
    private val elasticRepository: UserElasticRepository
) : UserDetailsManager {

    override fun loadUserByUsername(username: String): UserDetails {
        val opt = elasticRepository.findById(username)
        if(opt.isEmpty) throw IllegalStateException()
        else return UserDetailsImpl(opt.get())
    }

    override fun createUser(user: UserDetails) {
        TODO("Not yet Implemented")
    }

    override fun updateUser(user: UserDetails) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(username: String) = elasticRepository.deleteById(username)

    override fun changePassword(oldPassword: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun userExists(username: String): Boolean {
        return elasticRepository.existsById(username)
    }
}

class UserDetailsImpl(
    private val user: UserDoc
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String = user.loginToken

    override fun getUsername(): String = user.userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = getCurrentTimeSeconds() > user.tokenExpireDate

    override fun isEnabled(): Boolean = true
}*/