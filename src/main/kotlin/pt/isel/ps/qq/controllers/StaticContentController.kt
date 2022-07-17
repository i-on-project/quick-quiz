package pt.isel.ps.qq.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@Controller
class StaticContentController {
    @GetMapping(
        "/{path:[^.]*}",
        "/{path:[^.]*}/{path:[^.]*}",
        "/{path:[^.]*}/{path:[^.]*}/{path:[^.]*}",
        "/{path:[^.]*}/{path:[^.]*}/{path:[^.]*}/{path:[^.]*}"
    )
    fun redirect(): String {
        return "forward:/index.html"
    }

}