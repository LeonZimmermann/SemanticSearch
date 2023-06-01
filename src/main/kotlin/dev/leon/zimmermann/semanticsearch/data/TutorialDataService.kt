package dev.leon.zimmermann.semanticsearch.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.leon.zimmermann.semanticsearch.data.tutorial.DataService
import io.weaviate.client.v1.data.model.WeaviateObject
import java.net.URL


class TutorialDataService: DataService {

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

    inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

}
