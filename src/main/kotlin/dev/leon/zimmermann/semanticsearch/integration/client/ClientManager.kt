package dev.leon.zimmermann.semanticsearch.integration.client

import dev.leon.zimmermann.semanticsearch.DatabaseClient
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import org.slf4j.LoggerFactory
import java.io.IOException

class ClientManager(hostName: String, port: String, ) : DatabaseClient {
    companion object {
        private const val SCHEME = "http"
        private const val CONNECTION_TIMEOUT = 60 * 5
        private const val CONNECTION_REQUEST_TIMEOUT = 60 * 5
        private const val CONNECTION_SOCKET_TIMEOUT = 60 * 5
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    override val client = WeaviateClient(
        Config(
            SCHEME,
            "$hostName:$port",
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
            throw IOException("Error ${meta.error.statusCode}: ${meta.error.messages}")
        }
    }
}
