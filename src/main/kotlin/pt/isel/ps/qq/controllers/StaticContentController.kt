package pt.isel.ps.qq.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class StaticContentController {
   @RequestMapping(value = ["/{path:[^.]*}"])
    fun redirect(): String {
        return "forward:/index.html"
    }
}