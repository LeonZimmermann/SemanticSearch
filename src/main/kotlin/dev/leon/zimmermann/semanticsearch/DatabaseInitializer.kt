package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.base.WeaviateError
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.slf4j.LoggerFactory
import java.io.IOException

class DatabaseInitializer(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService
) {

    companion object {
        private const val WAIT_TIME_FOR_LIVE = 2000L
        private const val MAX_NUMBER_OF_TRIES = 30
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    fun initializeDatabase() {
        logger.debug("initializing database")
        waitUntilWeaviateIsLive()
        clearDatabase()
        initializeDatabaseScheme()
        initializeData()
        setReadyFlag()
        logger.debug("database initialization finished")
    }

    private fun waitUntilWeaviateIsLive() {
        var isLive = false
        var numberOfTries = 0
        do {
            Thread.sleep(WAIT_TIME_FOR_LIVE)
            val response = databaseClient.client.misc().liveChecker().run()
            if (response.error != null) {
                logger.error("Error ${response.error.statusCode}: ${response.error.messages}")
            } else {
                isLive = response.result
            }
            numberOfTries++
        } while (!isLive && numberOfTries <= MAX_NUMBER_OF_TRIES)
        if (!isLive) {
            throw IOException("Weaviate is still not live after ${WAIT_TIME_FOR_LIVE * MAX_NUMBER_OF_TRIES / 1000f} seconds")
        }
    }

    private fun clearDatabase() {
        doWithRetry(3) {
            databaseClient.client.schema().classDeleter()
                .withClassName(dataService.getDatabaseScheme().className)
                .run()
                .error
        }
        logger.debug("Successfully cleared database")
    }

    private fun initializeDatabaseScheme() {
        doWithRetry(3) {
            databaseClient.client.schema().classCreator()
                .withClass(dataService.getDatabaseScheme())
                .run()
                .error
        }
        logger.debug("Successfully initialized database scheme")
    }

    private fun readyClass(): WeaviateClass {
        return WeaviateClass.builder()
            .className("Ready")
            .properties(
                listOf(
                    Property.builder()
                        .name("ready-flag")
                        .dataType(listOf("boolean"))
                        .build()
                )
            )
            .build()
    }

    private fun initializeData() {
        dataService.getData().forEach {
            doWithRetry(3) {
                databaseClient.client.batch()
                    .objectsBatcher()
                    .withObject(it)
                    .run()
                    .error
            }
            logger.debug("Successfully added ${it.id} to ${it.className}")
        }
    }

    private fun doWithRetry(maxRetries: Int, action: () -> WeaviateError?) {
        var error: WeaviateError?
        var retries = 0
        do {
            error = action()
            if (error != null) {
                retries++
            }
        } while (error != null && retries < maxRetries)
        if (error != null) {
            // TODO: Throw exception
            logger.debug("Error ${error.statusCode}: ${error.messages}")
        }
    }

    private fun setReadyFlag() {
        val result = databaseClient.client.data().creator().withClassName("Ready")
            .withProperties(mapOf("ready" to true)).run()
        if (result.error != null) {
            throw IOException("Error ${result.error.statusCode}: ${result.error.messages}")
        }
    }

}
