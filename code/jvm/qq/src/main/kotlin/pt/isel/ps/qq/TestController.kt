package pt.isel.ps.qq.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping(“/api/web/guest”)
class TestController {
    @PostMapping("/test")
    @ResponseStatus(HttpStatus.CREATED)
    private fun create(@RequestBody resource: String): Long? {
        //Preconditions.checkNotNull(resource)
        return 999
    }
}