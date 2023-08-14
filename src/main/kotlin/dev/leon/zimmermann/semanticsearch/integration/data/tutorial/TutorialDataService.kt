package dev.leon.zimmermann.semanticsearch.integration.data.tutorial

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.fromJson
import dev.leon.zimmermann.semanticsearch.integration.data.DataServiceHelper
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.misc.model.VectorIndexConfig.VectorIndexConfigBuilder
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.net.URL


class TutorialDataService : DataService {

    private val dataServiceHelper = DataServiceHelper()

    override fun getData(): Array<WeaviateObject> {
        val connection =
            URL("https://raw.githubusercontent.com/weaviate-tutorials/quickstart/main/data/jeopardy_tiny+vectors.json").openConnection()
        val input = connection.getInputStream()
        val gson = GsonBuilder().create()
        connection.connect()
        val text = input.bufferedReader().readText()
        println("Read text: $text")
        return gson.fromJson<Array<Map<String, Any>>>(text)
            .map {
                WeaviateObject.builder()
                    .className("Question")
                    .vector((it["Vector"] as ArrayList<Double>).map { number -> number.toFloat() }.toTypedArray())
                    .properties(
                        mapOf(
                            "category" to it["Category"],
                            "question" to it["Question"],
                            "answer" to it["Answer"]
                        )
                    ).build()
            }.toTypedArray()
    }

    override fun getDatabaseScheme(): WeaviateClass {
        return WeaviateClass.builder()
            .className("Question")
            .properties(
                dataServiceHelper.buildProperties(
                    mapOf(
                        "category" to "text",
                        "question" to "text",
                        "answer" to "text"
                    )
                )
            )
            .vectorizer("text2vec-transformers")
            .build()
    }

    override fun parseResult(result: Any): Array<Map<String, String>> {
        return ((result as LinkedTreeMap<*, *>)["Get"] as LinkedTreeMap<*, List<*>>)["Document"]
            .orEmpty()
            .map { it as LinkedTreeMap<String, String> }
            .map {
                mapOf(
                    "question" to (it["question"] ?: ""),
                    "answer" to (it["answer"] ?: "")
                )
            }
            .toTypedArray()
    }
}
