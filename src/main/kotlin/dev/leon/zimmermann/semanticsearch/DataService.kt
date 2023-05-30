package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.v1.data.model.WeaviateObject


interface DataService {
    fun getData(): Array<WeaviateObject>
}
