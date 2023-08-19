package dev.leon.zimmermann.semanticsearch.integration.client

import dev.leon.zimmermann.semanticsearch.DatabaseClient
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import org.slf4j.LoggerFactory

class ClientManager: DatabaseClient {
    companion object {
        private const val SCHEME = "http"
        private const val HOST = "weaviate:2000"
        private const val CONNECTION_TIMEOUT = 60 * 5
        private const val CONNECTION_REQUEST_TIMEOUT = 60 * 5
        private const val CONNECTION_SOCKET_TIMEOUT = 60 * 5
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    override val client = WeaviateClient(
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
            logger.debug("meta.hostname: ${meta.result.hostname}")
            logger.debug("meta.version: ${meta.result.version}")
            logger.debug("meta.modules: ${meta.result.modules}")
        } else {
            logger.error("Error ${meta.error.statusCode}: ${meta.error.messages}")
        }
    }
}
