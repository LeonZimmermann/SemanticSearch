package dev.leon.zimmermann.semanticsearch.data.confluence

import dev.leon.zimmermann.semanticsearch.data.tutorial.DataService
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import io.weaviate.client.v1.data.model.WeaviateObject
import java.io.File
import java.nio.charset.Charset


class ConfluenceDataService(private val pathToFolder: String, textPreprocessor: TextPreprocessor) :
    DataService {

    companion object {
        const val DOCUMENT_CLASS = "Document"

        const val DOCUMENT_TITLE = "documentTitle"
        const val PARAGRAPH_TAG = "p"
        const val H1_TAG = "h1"
        const val H2_TAG = "h2"
    }

    private val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)

    override fun getData(): Array<WeaviateObject> {
        val file = File(pathToFolder)
        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist (\"$pathToFolder\")")
        }
        if (!file.isDirectory) {
            throw IllegalArgumentException("File has to be a directory (\"$pathToFolder\")")
        }
        return file.listFiles()
            ?.filter { it.isFile }
            ?.map { it.name to it.readText(Charset.defaultCharset()) }
            ?.map { it.first to confluenceDataPreprocessor.apply(it.second) }
            ?.map { addTitleToProperties(it) }
            ?.map { createWeaviateObject(it) }
            ?.toTypedArray()
            ?: throw RuntimeException("Could not get files from directory (\"$pathToFolder\")")
    }

    private fun addTitleToProperties(pairOfTitleAndPropertyMap: Pair<String, Map<String, *>>): Map<String, Any?> {
        val map = pairOfTitleAndPropertyMap.second.toMutableMap()
        map[DOCUMENT_TITLE] = pairOfTitleAndPropertyMap.first
        return map.toMap()
    }

    private fun createWeaviateObject(properties: Map<String, *>) =
        WeaviateObject.builder()
            .className(DOCUMENT_CLASS)
            .properties(properties)
            .build()
}
