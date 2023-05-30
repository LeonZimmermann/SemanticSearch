package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.DOCUMENT_CLASS
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.DOCUMENT_TITLE
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.PARAGRAPH_TAG
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.WeaviateClass

fun main(args: Array<String>) {
    val client = WeaviateClient(Config("http", "localhost:8080"))
    checkClientStatus(client)
    initializeDatabaseScheme(client)
    initializeData(client)
    makeQuery(client)
}

private fun makeQuery(client: WeaviateClient) {
    val result = client.graphQL().get()
        .withClassName(DOCUMENT_CLASS)
        .withFields(*buildFields(PARAGRAPH_TAG, H1_TAG, H2_TAG, DOCUMENT_TITLE))
        .withLimit(5)
        .run()
    if (result.error != null) {
        println(result.error)
    } else {
        println(result.result)
    }
}

private fun checkClientStatus(client: WeaviateClient) {
    val meta = client.misc().metaGetter().run()
    if (meta.error == null) {
        System.out.printf("meta.hostname: %s\n", meta.result.hostname)
        System.out.printf("meta.version: %s\n", meta.result.version)
        System.out.printf("meta.modules: %s\n", meta.result.modules)
    } else {
        System.out.printf("Error: %s\n", meta.error.messages)
    }
}

private fun initializeDatabaseScheme(client: WeaviateClient) {
    client.schema().classCreator()
        .withClass(
            WeaviateClass.builder()
                .className(DOCUMENT_CLASS)
                .vectorizer("text2vec-transformers")
                .build()
        )
        .run()
}

private fun initializeData(client: WeaviateClient) {
    val result = client.batch()
        .objectsBatcher()
        .withObjects(
            *ConfluenceDataService(
                "C:\\Users\\lezimmermann\\Downloads\\SD",
                "/stop_words_german.txt").getData()
        )
        .run()
    if (result.error != null) {
        throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages.toString()}")
    } else {
        println(result.result.joinToString("\n"))
    }
}

private fun buildFields(vararg names: String): Array<Field> {
    return names.map { Field.builder().name(it).build() }.toTypedArray()
}
