package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import io.weaviate.client.v1.data.model.WeaviateObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class QueryBuilder(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService,
    private val textPreprocessor: TextPreprocessor,
) {

    private val logger = LoggerFactory.getLogger(javaClass.toString())

    fun makeQuery(numberOfResults: Int, input: String): Array<Map<String, String>> {
        val concepts = "[\"${textPreprocessor.preprocess(input)}\"]"
        val query = """
            {
                Get {
                    ${dataService.getDatabaseScheme().className}(
                          limit: $numberOfResults
                          nearText: {
                            concepts: $concepts
                          }
                    ) {
                      ${dataService.getDatabaseScheme().properties.joinToString("\n") { it.name }}
                      _additional {
                        id
                        distance
                      }
                    }
                }
            }
        """.trimIndent()
        logger.debug("Concepts: $concepts")
        logger.debug("Query: $query")
        val result = databaseClient.client.graphQL().raw().withQuery(query).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            if (result.result.data != null) {
                println("Query result: ${result.result.data}")
                return dataService.parseResult(result.result.data)
                    .sortedBy { it["distance"]?.toFloat() }
                    .toTypedArray()
            } else {
                throw IllegalArgumentException("Query result was null")
            }
        }
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

}
