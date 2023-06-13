package dev.leon.zimmermann.semanticsearch.data

import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass


interface DataService {
    fun getData(): Array<WeaviateObject>
    fun getDatabaseScheme(): WeaviateClass

    fun buildProperties(namesAndDataTypes: Map<String, String>): List<Property> {
        return namesAndDataTypes.map { entry ->
            Property.builder()
                .name(entry.key)
                .dataType(listOf(entry.value))
                .build()
        }.toList()
    }
}
