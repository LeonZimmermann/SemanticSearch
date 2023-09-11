package dev.leon.zimmermann.semanticsearch.integration.api

import dev.leon.zimmermann.semanticsearch.QueryService
import io.weaviate.client.v1.data.model.WeaviateObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data")
class DataController(private val queryService: QueryService) {

    @GetMapping("/get")
    fun getData(@RequestParam("className") className: String): ResponseEntity<List<WeaviateObject>> {
        return ResponseEntity.ok(queryService.getDataInClass(className))
    }
}
