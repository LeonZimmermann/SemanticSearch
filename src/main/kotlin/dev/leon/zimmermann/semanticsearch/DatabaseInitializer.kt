package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.slf4j.LoggerFactory
import java.io.IOException

class DatabaseInitializer(
    private val databaseClient: DatabaseClient,
    private val dataService: DataService
) {

    companion object {
        private const val BATCH_SIZE = 5
    }

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
        } else {
            logger.debug("Successfully cleared database")
        }
    }

    private fun initializeDatabaseScheme() {
        handleErrorsIfNecessary(databaseClient.client.schema().classCreator()
            .withClass(dataService.getDatabaseScheme())
            .run(), "Successfully initialized database scheme")
    }

    private fun handleErrorsIfNecessary(result: io.weaviate.client.base.Result<*>, successMessage: String) {
        if (result.error != null) {
            throw IOException("Error ${result.error.statusCode}: ${result.error.messages}")
        } else {
            logger.debug(successMessage)
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
        dataService.getData().forEach {
            val result = databaseClient.client.batch()
                .objectsBatcher()
                .withObject(it)
                .run()
            if (result.error != null) {
                throw IOException("Error ${result.error.statusCode}: ${result.error.messages}")
            } else {
                logger.debug("Successfully added ${it.id} to ${it.className}")
            }
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
