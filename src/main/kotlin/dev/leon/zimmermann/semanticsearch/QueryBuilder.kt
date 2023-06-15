package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import io.weaviate.client.WeaviateClient

class QueryBuilder(private val client: WeaviateClient, private val textPreprocessor: TextPreprocessor) {
    fun makeQuery(numberOfResults: Int, input: String): Any {
        val concepts = textPreprocessor.preprocess(input.split(" ").toTypedArray()).joinToString(", ") { "\"${it}\""}
        println("makeQuery: $concepts")
        val result = client.graphQL().raw().withQuery("""
            {
                Get {
                    Document(
                          limit: $numberOfResults
                          nearText: {
                            concepts: [$concepts]
                          }
                    ) {
                      ${ConfluenceDataService.DOCUMENT_URL}
                    }
                }
            }
        """.trimIndent()).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            return result.result
        }
    }
}
