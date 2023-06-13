package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import io.weaviate.client.WeaviateClient

class QueryBuilder(private val client: WeaviateClient, private val textPreprocessor: TextPreprocessor) {
    fun makeQuery(numberOfResults: Int, input: String): Any {
        val result = client.graphQL().raw().withQuery("""
            {
                Get {
                    Document(
                          limit: $numberOfResults
                          nearText: {
                            concepts: [${textPreprocessor.preprocess(input.split(" ").toTypedArray()).joinToString(", ") { "\"${it}\""}}]
                          }
                    ) {
                      ${ConfluenceDataService.TITLE_TAG}
                      ${ConfluenceDataService.DOCUMENT_URL}
                      _additional {
                        distance
                      }
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
