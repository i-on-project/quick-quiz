package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.utils.Uris



@Configuration
class MvcConfig(
    private val authService: AuthenticationService
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/static/**")
            .addResourceLocations("classpath:/static/")
    }
/*
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()
*/

   /* @Bean
    fun random(): Random = Random()*/

    @Bean
    fun hostName(): String = System.getenv("QQ_HOST") ?: "*"

    @Bean
    fun userFilterRegistrationBean(): FilterRegistrationBean<UserFilter> {
        val registrationBean: FilterRegistrationBean<UserFilter> = FilterRegistrationBean<UserFilter>()
        registrationBean.filter = UserFilter(authService)
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


