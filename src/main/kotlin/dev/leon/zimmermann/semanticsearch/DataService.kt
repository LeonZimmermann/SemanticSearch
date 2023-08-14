package dev.leon.zimmermann.semanticsearch

import com.google.gson.internal.LinkedTreeMap
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass


interface DataService {
    fun getData(): Array<WeaviateObject>
    fun getDatabaseScheme(): WeaviateClass
    fun parseQueryResult(queryResult: Any, parseAdditionals: (LinkedTreeMap<String, Any>) -> Map<String, String>): Array<Map<String, String>>
}
