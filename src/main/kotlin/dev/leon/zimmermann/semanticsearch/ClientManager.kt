package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.DataService
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass

class ClientManager(private val dataService: DataService) {

    companion object {
        private const val VECTORIZER = "text2vec-transformers"
        private const val WEAVIATE_TEXT_DATATYPE = "text"
    }

    val client = WeaviateClient(Config("http", "localhost:8080"))

    init {
        checkClientStatus()
        clearDatabase()
        initializeDatabaseScheme()
        initializeData()
    }

    private fun checkClientStatus() {
        val meta = client.misc().metaGetter().run()
        if (meta.error == null) {
            System.out.printf("meta.hostname: %s\n", meta.result.hostname)
            System.out.printf("meta.version: %s\n", meta.result.version)
            System.out.printf("meta.modules: %s\n", meta.result.modules)
        } else {
            System.out.printf("Error: %s\n", meta.error.messages)
        }
    }

    private fun clearDatabase() {
        client.schema().classDeleter()
            .withClassName(ConfluenceDataService.DOCUMENT_CLASS)
            .run()
    }

    private fun initializeDatabaseScheme() {
        client.schema().classCreator()
            .withClass(
                WeaviateClass.builder()
                    .className(ConfluenceDataService.DOCUMENT_CLASS)
                    .properties(
                        buildProperties(
                            mapOf(
                                ConfluenceDataService.DOCUMENT_URL to WEAVIATE_TEXT_DATATYPE,
                                ConfluenceDataService.H1_TAG to WEAVIATE_TEXT_DATATYPE,
                                ConfluenceDataService.H2_TAG to WEAVIATE_TEXT_DATATYPE,
                                ConfluenceDataService.PARAGRAPH_TAG to WEAVIATE_TEXT_DATATYPE
                            )
                        )
                    )
                    .vectorizer(VECTORIZER)
                    .build()
            )
            .run()
    }

    private fun buildProperties(namesAndDataTypes: Map<String, String>): List<Property> {
        return namesAndDataTypes.map { entry ->
            Property.builder()
                .name(entry.key)
                .dataType(listOf(entry.value))
                .build()
        }.toList()
    }

    private fun initializeData() {
        println("initializing data...")
        val result = client.batch()
            .objectsBatcher()
            .withObjects(*dataService.getData())
            .run()
        if (result.error != null) {
            throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages}")
        } else {
            println(result.result.joinToString("\n"))
        }
    }
}
