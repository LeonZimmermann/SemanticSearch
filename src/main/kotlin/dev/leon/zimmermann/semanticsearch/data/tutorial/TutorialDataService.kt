package dev.leon.zimmermann.semanticsearch.data.tutorial

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.leon.zimmermann.semanticsearch.data.DataService
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.net.URL


class TutorialDataService : DataService {

    override fun getData(): Array<WeaviateObject> {
        val connection =
            URL("https://raw.githubusercontent.com/weaviate-tutorials/quickstart/main/data/jeopardy_tiny.json").openConnection()
        val input = connection.getInputStream()
        val gson = GsonBuilder().create()
        connection.connect()
        val text = input.bufferedReader().readText()
        println("Read text: $text")
        return gson.fromJson<Array<Map<String, String>>>(text)
            .map {
                WeaviateObject.builder()
                    .className("Question")
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
                buildProperties(
                    mapOf(
                        "category" to "text",
                        "question" to "text",
                        "answer" to "text"
                    )
                )
            ).build()
    }

    inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

}
