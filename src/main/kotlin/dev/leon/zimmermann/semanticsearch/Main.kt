package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.DefaultTextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.IdentityTextPreprocessor

fun main(args: Array<String>) {
    val dataPath = "C:\\Users\\lezimmermann\\Downloads\\testdata"
    val textPreprocessor = IdentityTextPreprocessor()
    val dataService = ConfluenceDataService(dataPath, textPreprocessor)
    val clientManager = ClientManager(dataService)
    val queryBuilder = QueryBuilder(clientManager.client)
    println(queryBuilder.makeQuery(1, "can edit this roadm"))
}
