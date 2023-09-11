package dev.leon.zimmermann.semanticsearch.integration.api

import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.DatabaseClient
import dev.leon.zimmermann.semanticsearch.DatabaseInitializer
import dev.leon.zimmermann.semanticsearch.QueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
class SearchController(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService,
    private val queryService: QueryService
) {

    @PostMapping("/initialize")
    fun initialize() {
        DatabaseInitializer(databaseClient, dataService).initializeDatabase()
    }

    @GetMapping("/search")
    fun search(
        @RequestParam("query") query: String,
        @RequestParam("className") className: String,
        @RequestParam("properties") properties: String
    ): ResponseEntity<Array<Map<String, String>>> {
        // TODO Validate inputs?
        return ResponseEntity.ok(queryService.makeQuery(5, query, className, properties))
    }

    @PostMapping("/ask")
    fun ask(@RequestParam("query") question: String,
            @RequestParam("className") className: String,
            @RequestParam("properties") properties: String): ResponseEntity<String> {
        // TODO Validate inputs?
        return ResponseEntity.ok(queryService.askQuestion(question, className, properties))
    }

    @PostMapping("/document")
    fun document(@RequestBody documentUrl: String): ResponseEntity<String?> {
        // TODO Validate documentUrl
        return ResponseEntity.ok(File(documentUrl).readText())
    }
}
