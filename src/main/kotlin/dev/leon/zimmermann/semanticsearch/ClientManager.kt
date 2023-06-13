package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient

class ClientManager {

    companion object {
        private const val SCHEME = "http"
        private const val HOST = "localhost:8080"
        private const val CONNECTION_TIMEOUT = 60
        private const val CONNECTION_REQUEST_TIMEOUT = 60 * 5
        private const val CONNECTION_SOCKET_TIMEOUT = 60 * 5
    }

    val client = WeaviateClient(
        Config(
            SCHEME,
            HOST,
            null,
            CONNECTION_TIMEOUT,
            CONNECTION_REQUEST_TIMEOUT,
            CONNECTION_SOCKET_TIMEOUT
        )
    )

    init {
        checkClientStatus()
    }

    private fun checkClientStatus() {
        val meta = client.misc().metaGetter().run()
        if (meta.error == null) {
            println("meta.hostname: ${meta.result.hostname}")
            println("meta.version: ${meta.result.version}")
            println("meta.modules: ${meta.result.modules}")
        } else {
            println("Error ${meta.error.statusCode}: ${meta.error.messages}")
        }
    }
}
