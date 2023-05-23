package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.WeaviateClass

fun main(args: Array<String>) {
  val config =
    Config("http", "localhost:8080")
  val client = WeaviateClient(config)
  val meta = client.misc().metaGetter().run()
  if (meta.error == null) {
    System.out.printf("meta.hostname: %s\n", meta.result.hostname)
    System.out.printf("meta.version: %s\n", meta.result.version)
    System.out.printf("meta.modules: %s\n", meta.result.modules)
  } else {
    System.out.printf("Error: %s\n", meta.error.messages)
  }

  initalizeDatabase(client)

  val result = client.graphQL().get()
    .withClassName("Question")
    .withFields(*buildFields("question", "answer", "category"))
    .withNearText(NearTextArgument.builder()
      .concepts(arrayOf("new"))
      .build())
    .withLimit(2)
    .run()
    .result
  println(result.data)
}

private fun initalizeDatabase(client: WeaviateClient) {
  client.schema().classCreator()
    .withClass(WeaviateClass.builder()
      .className("Question")
      .vectorizer("text2vec-transformers")
      .build())
    .run()
  client.batch()
    .objectsBatcher()
    .withObjects(*DataService().getData())
    .run()
}

private fun buildFields(vararg names: String): Array<Field> {
  return names.map { Field.builder().name(it).build() }.toTypedArray()
}
