package pt.isel.ps.qq.controllers

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HomeControler : ErrorController {
        // implement ErrorController to handle 404 (error pages)
        // Always redirect to index.html page (only option)
        // Can handle all request (which end up in 404)
        @GetMapping("/error")
        fun error(): String {
            return "forward:/index.html"
        }

}