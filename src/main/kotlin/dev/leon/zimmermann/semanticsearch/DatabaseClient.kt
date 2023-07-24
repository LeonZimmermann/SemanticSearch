package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.WeaviateClient

interface DatabaseClient {
    val client: WeaviateClient
}
