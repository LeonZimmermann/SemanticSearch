package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.WeaviateClient

class QueryBuilder(private val client: WeaviateClient) {
    fun makeQuery(numberOfResults: Int, input: String): Any {
        val result = client.graphQL().raw().withQuery("""
            {
                Get {
                    Document(
                          limit: $numberOfResults
                          nearText: {
                            concepts: [${input.split(" ").joinToString(", ") { "\"${it}\""}}]
                          }
                    ) {
                      documentTitle
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
