package pt.isel.ps.qq.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.ps.qq.data.Field
import pt.isel.ps.qq.data.InputModel
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest


class RequestBodyParser(private val mapper: ObjectMapper) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == InputModel::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        val body  = request.reader.lines().collect(Collectors.joining(System.lineSeparator()))

        val input = mapper.readValue(body, InputModel::class.java)
        val map = input.fields.associate { it.name to it.value }


        return when(parameter.parameterType) {
            InputModel::class.java -> input
            else -> null
        }
    }

    //TODO("")
}