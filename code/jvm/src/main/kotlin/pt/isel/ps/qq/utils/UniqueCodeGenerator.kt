package pt.isel.ps.qq.utils

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class UniqueCodeGenerator {

    private val idCounter = AtomicInteger()

    fun createID(): Int {
        return idCounter.getAndIncrement()
    }
}