package dev.leon.zimmermann.semanticsearch

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import io.weaviate.client.WeaviateClient
import org.slf4j.LoggerFactory

class QueryBuilder(private val client: WeaviateClient, private val textPreprocessor: TextPreprocessor) {

    private val logger = LoggerFactory.getLogger(javaClass.toString())

    fun makeQuery(numberOfResults: Int, input: String): Array<Document> {
        val concepts = textPreprocessor.preprocess(input.split(" ").toTypedArray()).joinToString(", ") { "\"${it}\""}
        logger.debug("makeQuery: $concepts")
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
                      ${ConfluenceDataService.TITLE_TAG}
                      ${ConfluenceDataService.H1_TAG}
                      ${ConfluenceDataService.H2_TAG}
                      ${ConfluenceDataService.PARAGRAPH_TAG}
                      _additional {
                        id
                        distance
                      }
                    }
                }
            }
        """.trimIndent()).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            return parseResult(result.result.data)
        }
    }

    private fun parseResult(result: Any): Array<Document> {
        return ((result as LinkedTreeMap<*, *>)["Get"] as LinkedTreeMap<*, List<*>>)["Document"]
            .orEmpty()
            .map { it as LinkedTreeMap<String, String> }
            .map { Document(it["id"] ?: "", it["documentUrl"] ?: "", it["title"] ?: "", it["h1"] ?: "", it["h2"] ?: "", it["p"] ?: "", it["distance"]?.toFloat() ?: -1f) }
            .toTypedArray()
    }

}
