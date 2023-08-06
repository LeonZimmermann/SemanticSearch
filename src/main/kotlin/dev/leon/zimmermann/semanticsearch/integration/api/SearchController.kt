package dev.leon.zimmermann.semanticsearch.integration.api

import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.DatabaseClient
import dev.leon.zimmermann.semanticsearch.QueryBuilder
import dev.leon.zimmermann.semanticsearch.integration.api.dto.DocumentView
import dev.leon.zimmermann.semanticsearch.startup.DatabaseInitializer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class SearchController(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService,
    private val queryBuilder: QueryBuilder
) {

    @PostMapping("/initialize")
    fun initialize() {
        DatabaseInitializer(databaseClient, dataService).initializeDatabase()
    }

    @GetMapping("/search")
    fun search(@RequestBody query: String): List<DocumentView> {
        // TODO Validate query?
        return queryBuilder.makeQuery(5, query)
            .map { DocumentView(it.documentUrl, it.titleTags) }
    }

    @GetMapping("/document")
    fun document(@RequestBody documentUrl: String): String? {
        // TODO Validate documentUrl
        return javaClass.getResourceAsStream("/sites/$documentUrl")
            ?.bufferedReader()
            ?.readText()
    }
}
