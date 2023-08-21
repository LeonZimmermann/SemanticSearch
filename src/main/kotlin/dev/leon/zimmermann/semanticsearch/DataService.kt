package dev.leon.zimmermann.semanticsearch

import com.google.gson.internal.LinkedTreeMap
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.util.stream.Stream


interface DataService {
    fun getData(): Stream<WeaviateObject>
    fun getDatabaseScheme(): WeaviateClass
    fun getMapOfData(sourceMap: LinkedTreeMap<String, String>): Map<String, String>
}
