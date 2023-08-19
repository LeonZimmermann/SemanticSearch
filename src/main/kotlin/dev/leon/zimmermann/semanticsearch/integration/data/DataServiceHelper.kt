package dev.leon.zimmermann.semanticsearch.integration.data

import io.weaviate.client.v1.schema.model.Property

class DataServiceHelper {
    fun buildProperties(namesAndDataTypes: Map<String, String>): List<Property> {
        return namesAndDataTypes.map { entry ->
            Property.builder()
                .name(entry.key)
                .indexInverted(true)
                .dataType(listOf(entry.value))
                .build()
        }.toList()
    }
}
