package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.DOCUMENT_CLASS
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H1_TAG
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
    val result = client.graphQL().get()
        .withClassName(DOCUMENT_CLASS)
        .withFields(*buildFields(PARAGRAPH_TAG, H1_TAG))
        .withNearText(
            NearTextArgument.builder()
                .autocorrect(true)
                .concepts(arrayOf(""))
                .build()
        )
        .withLimit(5)
        .run()
    if (result.error != null) {
        println(result.error)
    } else {
        println(result.result.data)
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
    client.batch()
        .objectsBatcher()
        .withObjects(
            *ConfluenceDataService(
                "C:\\Users\\lezimmermann\\Downloads\\SD",
                "/stop_words_german.txt"
            ).getData()
        )
        .run()
}

private fun buildFields(vararg names: String): Array<Field> {
    return names.map { Field.builder().name(it).build() }.toTypedArray()
}
