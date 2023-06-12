package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.DefaultTextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.IdentityTextPreprocessor
import java.util.*
import kotlin.system.exitProcess

fun main() {
    val dataPath = "C:\\Users\\lezimmermann\\Downloads\\mdk-architektur"
    val textPreprocessor = DefaultTextPreprocessor("/stop_words_german.txt")
    val dataService = ConfluenceDataService(dataPath, textPreprocessor)
    val clientManager = ClientManager(dataService)
    val queryBuilder = QueryBuilder(clientManager.client, textPreprocessor)
    val scanner = Scanner(System.`in`)
    while (scanner.hasNext()) {
        val input = scanner.next()
        if (input == "exit") {
            exitProcess(0)
        } else {
            println(queryBuilder.makeQuery(3, input))
        }
    }
}
