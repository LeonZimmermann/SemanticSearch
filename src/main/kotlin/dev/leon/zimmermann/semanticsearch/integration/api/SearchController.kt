package dev.leon.zimmermann.semanticsearch.integration.api

import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.DatabaseClient
import dev.leon.zimmermann.semanticsearch.DatabaseInitializer
import dev.leon.zimmermann.semanticsearch.QueryBuilder
import dev.leon.zimmermann.semanticsearch.integration.api.dto.DocumentView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class SearchController(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService,
    private val queryBuilder: QueryBuilder
) {

    @PostMapping("/initialize")
    fun initialize() {
        DatabaseInitializer(databaseClient, dataService).initializeDatabase()
    }

    @PostMapping("/search")
    fun search(@RequestBody query: String): ResponseEntity<Array<Map<String, String>>> {
        // TODO Validate query?
        return ResponseEntity.ok(queryBuilder.makeQuery(5, query))
    }

    @PostMapping("/document")
    fun document(@RequestBody documentUrl: String): ResponseEntity<String?> {
        // TODO Validate documentUrl
        return ResponseEntity.ok(File(documentUrl).readText())
    }
}
