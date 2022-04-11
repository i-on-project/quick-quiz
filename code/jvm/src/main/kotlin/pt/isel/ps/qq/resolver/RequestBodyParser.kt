package pt.isel.ps.qq.resolver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.ps.qq.data.GiveAnswer
import pt.isel.ps.qq.data.InputModel
import pt.isel.ps.qq.data.JoinSession
import pt.isel.ps.qq.data.User
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

class RequestBodyParser(
    private val mapper: ObjectMapper,
    private val processor: InputModelProcessor
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return  when(parameter.parameterType){
            JoinSession::class.java -> true
            GiveAnswer::class.java -> true
            User::class.java -> true
            else -> false
        }
    }

    //TODO try catch
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        val body = request.reader.lines().collect(Collectors.joining(System.lineSeparator()))
        val input = mapper.readValue(body, InputModel::class.java)
        return processor.getImpOf(input.getFieldsMap(), parameter.parameterType)

    }
}