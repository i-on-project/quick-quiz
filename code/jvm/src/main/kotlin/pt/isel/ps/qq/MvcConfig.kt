package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.filters.UserFilter
import pt.isel.ps.qq.resolver.InputModelProcessor
import pt.isel.ps.qq.resolver.RequestBodyParser

@Component
class MvcConfig : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(RequestBodyParser(ObjectMapper().registerKotlinModule(), InputModelProcessor()))
    }

    @Bean
    fun objectMapperBean(): ObjectMapper = ObjectMapper().registerKotlinModule()

    @Bean
    fun loggingFilter(mapper:ObjectMapper): FilterRegistrationBean<UserFilter>? {
        val registrationBean: FilterRegistrationBean<UserFilter> =
            FilterRegistrationBean<UserFilter>()
        registrationBean.filter = UserFilter(mapper)
        registrationBean.addUrlPatterns("/api/web/v1.0/user/**")
        //registrationBean.order = 2
        return registrationBean
    }

}
