package dev.leon.zimmermann.semanticsearch.startup

import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.DatabaseClient
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
        val response = databaseClient.client.schema().classCreator()
            .withClass(dataService.getDatabaseScheme())
            .run()
        if (response.error != null) {
            throw RuntimeException("Error ${response.error.statusCode}: ${response.error.messages.}")
        }
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

    private inline fun <reified T> arrayAsBatches(array: Array<T>, batchSize: Int): Array<Array<T>> {
        return if (array.size < batchSize) {
            arrayOf(array)
        } else {
            val list = mutableListOf<Array<T>>()
            for (i in 0 until (array.size / batchSize)) {
                val currentStart = i * batchSize
                val currentEnd = (i + 1) * batchSize - 1
                list.add(array.sliceArray(IntRange(currentStart, currentEnd)))
            }
            val lastStart = array.size - array.size % batchSize - 1
            val lastEnd = array.size - 1
            list.add(array.sliceArray(IntRange(lastStart, lastEnd)))
            list.toTypedArray()
        }
    }

}
