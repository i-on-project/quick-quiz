package pt.isel.ps.qq.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
interface IGuestService {
    fun processBody(body: Any?) : String;
}

@Component
class GuestService : IGuestService {

    override fun processBody(body: Any?) : String {
        return when(body) {
            null -> "Body processed is Null"
            else -> ""
        }
    }
}