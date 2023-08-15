package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.slf4j.LoggerFactory

class DatabaseInitializer(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun initializeDatabase() {
        logger.debug("initializing database")
        clearDatabase()
        initializeDatabaseScheme()
        initializeData()
        setReadyFlag()
        logger.debug("database initialization finished")
    }

    private fun clearDatabase() {
        val response = databaseClient.client.schema().classDeleter()
            .withClassName(dataService.getDatabaseScheme().className)
            .run()
        if (response.error != null) {
            logger.error("Error ${response.error.statusCode}: ${response.error.messages}")
        }
    }

    private fun initializeDatabaseScheme() {
        handleErrorsIfNecessary(databaseClient.client.schema().classCreator()
            .withClass(dataService.getDatabaseScheme())
            .run())
    }

    private fun handleErrorsIfNecessary(result: io.weaviate.client.base.Result<*>) {
        if (result.error != null) {
            throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages}")
        }
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
        for (batch in arrayAsBatches(dataService.getData(), 5)) {
            val result = databaseClient.client.batch()
                .objectsBatcher()
                .withObjects(*batch)
                .run()
            if (result.error != null) {
                throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages}")
            } else {
                logger.debug(result.result.joinToString("\n"))
            }
        }
    }

    private fun setReadyFlag() {
        val result = databaseClient.client.data().creator().withClassName("Ready")
            .withProperties(mapOf("ready" to true)).run()
        if (result.error != null) {
            throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages}")
        }
    }

}
