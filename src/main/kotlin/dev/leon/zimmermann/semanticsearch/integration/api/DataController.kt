package dev.leon.zimmermann.semanticsearch.integration.api

import dev.leon.zimmermann.semanticsearch.QueryBuilder
import io.weaviate.client.v1.data.model.WeaviateObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data")
class DataController(private val queryBuilder: QueryBuilder) {

    @GetMapping("/get")
    fun getData(): ResponseEntity<List<WeaviateObject>> {
        return ResponseEntity.ok(queryBuilder.getDataInClass())
    }
}
