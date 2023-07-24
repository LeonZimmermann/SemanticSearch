package dev.leon.zimmermann.semanticsearch.integration.web

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CustomErrorController: ErrorController {
    @GetMapping("/error")
    fun error(): String {
        return "error"
    }
}
