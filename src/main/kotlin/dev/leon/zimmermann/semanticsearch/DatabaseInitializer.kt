package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.DataService

class DatabaseInitializer(
    private val clientManager: ClientManager,
    private val dataService: DataService
) {

    fun initializeDatabase() {
        clearDatabase()
        initializeDatabaseScheme()
        initializeData()
    }

    private fun clearDatabase() {
        val response = clientManager.client.schema().classDeleter()
            .withClassName(dataService.getDatabaseScheme().className)
            .run()
        if (response.error != null) {
            println("Error ${response.error.statusCode}: ${response.error.messages}")
        }
    }

    private fun initializeDatabaseScheme() {
        val response = clientManager.client.schema().classCreator()
            .withClass(dataService.getDatabaseScheme())
            .run()
        if (response.error != null) {
            throw RuntimeException("Error ${response.error.statusCode}: ${response.error.messages}")
        }
    }

    private fun initializeData() {
        for (batch in arrayAsBatches(dataService.getData(), 5)) {
            val result = clientManager.client.batch()
                .objectsBatcher()
                .withObjects(*batch)
                .run()
            if (result.error != null) {
                throw RuntimeException("Error ${result.error.statusCode}: ${result.error.messages}")
            } else {
                println(result.result.joinToString("\n"))
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
