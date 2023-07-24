package dev.leon.zimmermann.semanticsearch.integration.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SearchController {

    @GetMapping("/index")
    fun search(): String {
        return "search"
    }
}
