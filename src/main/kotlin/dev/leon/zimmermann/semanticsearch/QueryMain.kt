package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.preprocessing.impl.DefaultTextPreprocessor
import java.util.*
import kotlin.system.exitProcess

fun main() {
    val clientManager = ClientManager()
    val textPreprocessor = DefaultTextPreprocessor("/stop_words_german.txt")
    val queryBuilder = QueryBuilder(clientManager.client, textPreprocessor)
    val scanner = Scanner(System.`in`)
    while (scanner.hasNext()) {
        val input = scanner.nextLine()
        if (input == "exit") {
            exitProcess(0)
        } else {
            if (input.startsWith("query:")) {
                queryBuilder.makeQuery(5, input)
                    .sortedBy { it.distance }
                    .joinToString("\n") { it.titleTags }
                    .let { println(it) }
            } else if (input.startsWith("question:")) {
                println(queryBuilder.askQuestion(input))
            } else {
                throw IllegalArgumentException("Input has to start either with query: or question:. Input was \"$input\"")
            }

        }
    }
}
