package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.data.tutorial.DataService
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.schema.model.WeaviateClass

class ClientManager(private val dataService: DataService) {

    companion object {
        private const val VECTORIZER = "text2vec-transformers"
    }

    val client = WeaviateClient(Config("http", "localhost:8080"))

    init {
        checkClientStatus()
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

    private fun initializeDatabaseScheme() {
        client.schema().classCreator()
            .withClass(
                WeaviateClass.builder()
                    .className(ConfluenceDataService.DOCUMENT_CLASS)
                    .vectorizer(VECTORIZER)
                    .build()
            )
            .run()
    }

    private fun initializeData() {
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
