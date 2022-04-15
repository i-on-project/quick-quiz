package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.qq.resolver.InputModelProcessor
import pt.isel.ps.qq.resolver.RequestBodyParser

@Component
class MvcConfig : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(RequestBodyParser(ObjectMapper().registerKotlinModule(), InputModelProcessor()))
    }

    @Bean
    fun objectMapperBean(): ObjectMapper = ObjectMapper().registerKotlinModule()

}
