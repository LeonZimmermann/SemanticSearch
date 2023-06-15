package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.DefaultTextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.IdentityTextPreprocessor

fun main() {
    val dataPath = "C:\\Users\\lezimmermann\\Downloads\\mdk"
    val textPreprocessor = DefaultTextPreprocessor("/stop_words_german.txt")
    val dataService = ConfluenceDataService(dataPath, textPreprocessor)
    val clientManager = ClientManager()
    DatabaseInitializer(clientManager, dataService).initializeDatabase()
}
