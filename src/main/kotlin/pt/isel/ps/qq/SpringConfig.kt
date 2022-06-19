package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.lang.Nullable
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.resource.ResourceResolver
import org.springframework.web.servlet.resource.ResourceResolverChain
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.repositories.UserRepository
import pt.isel.ps.qq.utils.Uris
import java.io.IOException
import java.util.*
import javax.mail.Session
import javax.servlet.http.HttpServletRequest


@Configuration
class MvcConfig(
    private val userRepository: UserRepository,
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
        registrationBean.filter = UserFilter(userRepository, scope)
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
   override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/static/**")
            .addResourceLocations("classpath:/static/")
    }


}

