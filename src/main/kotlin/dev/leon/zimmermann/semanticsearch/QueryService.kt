package dev.leon.zimmermann.semanticsearch

import com.google.gson.internal.LinkedTreeMap
import dev.leon.zimmermann.semanticsearch.configuration.SearchConfiguration
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService
import io.weaviate.client.v1.data.model.WeaviateObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class QueryService(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService,
    private val searchConfiguration: SearchConfiguration,
) {

    private val logger = LoggerFactory.getLogger(javaClass.toString())

    fun makeQuery(numberOfResults: Int, input: String): Array<Map<String, String>> {
        val concepts = "\"$input\""
        val query = createQuery(numberOfResults, concepts)
        logger.debug("Concepts: $concepts")
        logger.debug("Query: $query")
        val result = databaseClient.client.graphQL().raw().withQuery(query).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            if (result.result.data != null) {
                println("Query result: ${result.result.data}")
                return parseQueryResult(result.result.data)
                    .sortedByDescending { it["score"]?.toFloat() }
                    .toTypedArray()
            } else {
                throw IllegalArgumentException("Query result was null. Concepts: $concepts")
            }
        }
    }

    private fun createQuery(numberOfResults: Int, concepts: String) = """
        {
            Get {
                ${dataService.getDatabaseScheme().className}(
                      limit: $numberOfResults
                      hybrid: {
                        query: $concepts
                        alpha: ${searchConfiguration.alpha}
                      }
                ) {
                  ${dataService.getDatabaseScheme().properties.joinToString("\n") { it.name }}
                  _additional {
                    score
                    explainScore
                  }
                }
            }
        }
    """.trimIndent()

    private fun parseQueryResult(queryResult: Any): Array<Map<String, String>> {
        return ((queryResult as LinkedTreeMap<*, *>)["Get"] as LinkedTreeMap<*, List<*>>)[dataService.getDatabaseScheme().className]
            .orEmpty()
            .map { it as LinkedTreeMap<String, String> }
            .map {
                val result = dataService.getMapOfData(it).toMutableMap()
                val additionalSourceMap = it["_additional"] as? LinkedTreeMap<String, Any>
                if (additionalSourceMap != null) {
                    val additionalMap = mapOf(
                        "score" to additionalSourceMap["score"].toString(),
                        "explainScore" to additionalSourceMap["explainScore"].toString()
                    )
                    result.putAll(additionalMap)
                }
                result
            }
            .toTypedArray()
    }

    fun getDataInClass(): List<WeaviateObject> {
        val result = databaseClient.client.data().objectsGetter()
            .withClassName(dataService.getDatabaseScheme().className)
            .withVector()
            .run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            println("Query result: ${result.result}")
            return result.result
        }
    }

    fun askQuestion(question: String): String {
        val result = databaseClient.client.graphQL().raw().withQuery("""
            {
              Get {
                ${dataService.getDatabaseScheme().className}(
                  ask: {
                    question: "$question",
                    properties: ["${ConfluenceDataService.PARAGRAPH_TAG}"]
                    rerank: true
                  },
                  limit: 1
                ) {
                  ${dataService.getDatabaseScheme().properties.joinToString("\n") { it.name }}
                  _additional {
                    answer {
                      hasAnswer
                      certainty
                      property
                      result
                      startPosition
                      endPosition
                    }
                  }
                }
              }
            }
        """.trimIndent()).run()
        if (result.error != null) {
            throw IOException(result.error.toString())
        } else {
            return result.toString()
        }
    }

}
