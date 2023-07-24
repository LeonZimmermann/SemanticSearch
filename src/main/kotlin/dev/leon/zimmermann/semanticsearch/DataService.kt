package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass


interface DataService {
    fun getData(): Array<WeaviateObject>
    fun getDatabaseScheme(): WeaviateClass
}
