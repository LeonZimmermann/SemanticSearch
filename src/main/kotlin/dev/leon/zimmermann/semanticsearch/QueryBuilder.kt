package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService
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
        val concepts = textPreprocessor.preprocess(input.split(" ").toTypedArray())
            .joinToString(", ") { "\"${it}\"" }
        logger.debug("makeQuery: $concepts")
        val result = databaseClient.client.graphQL().raw().withQuery(
            """
            {
                Get {
                    ${dataService.getDatabaseScheme().className}(
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
        """.trimIndent()
        ).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            println("Query result: ${result.result.data}")
            return dataService.parseResult(result.result.data)
                .sortedBy { it["distance"] }
                .toTypedArray()
        }
    }

    fun getDataInClass(): List<WeaviateObject> {
        val result = databaseClient.client.data().objectsGetter()
            .withClassName(dataService.getDatabaseScheme().className).run()
        if (result.error != null) {
            throw RuntimeException(result.error.toString())
        } else {
            println("Query result: ${result.result}")
            return result.result
        }
    }

}
