package dev.leon.zimmermann.semanticsearch.data.confluence

import dev.leon.zimmermann.semanticsearch.data.DataService
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.io.File
import java.nio.charset.Charset
import kotlin.streams.asSequence


class ConfluenceDataService(private val pathToFolder: String, textPreprocessor: TextPreprocessor) :
    DataService {

    companion object {
        private const val VECTORIZER = "text2vec-transformers"
        private const val WEAVIATE_TEXT_DATATYPE = "text"

        const val DOCUMENT_CLASS = "Document"

        const val DOCUMENT_URL = "documentUrl"
        const val TITLE_TAG = "title"
        const val H1_TAG = "h1"
        const val H2_TAG = "h2"
        const val PARAGRAPH_TAG = "p"
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
            ?.parallelStream()
            ?.peek { println("reading file ${it.name}") }
            ?.map { it.toURI().path to it.readText(Charset.defaultCharset()) }
            ?.map { it.first to confluenceDataPreprocessor.apply(it.second) }
            ?.map { addUrlToProperties(it) }
            ?.peek { println("read data: $it") }
            ?.map { createWeaviateObject(it) }
            ?.asSequence()
            ?.toList()
            ?.toTypedArray()
            ?: throw RuntimeException("Could not get files from directory (\"$pathToFolder\")")
    }

    override fun getDatabaseScheme(): WeaviateClass {
        return WeaviateClass.builder()
            .className(DOCUMENT_CLASS)
            .properties(
                buildProperties(
                    mapOf(
                        DOCUMENT_URL to WEAVIATE_TEXT_DATATYPE,
                        TITLE_TAG to WEAVIATE_TEXT_DATATYPE,
                        H1_TAG to WEAVIATE_TEXT_DATATYPE,
                        H2_TAG to WEAVIATE_TEXT_DATATYPE,
                        PARAGRAPH_TAG to WEAVIATE_TEXT_DATATYPE
                    )
                )
            )
            .vectorizer(VECTORIZER)
            .build()
    }

    private fun addUrlToProperties(pairOfUrlAndPropertyMap: Pair<String, Map<String, *>>): Map<String, Any?> {
        val map = pairOfUrlAndPropertyMap.second.toMutableMap()
        val title = pairOfUrlAndPropertyMap.first
        map[DOCUMENT_URL] = if (title.startsWith(".")) title.substring(1, title.length) else title
        return map.toMap()
    }

    private fun createWeaviateObject(properties: Map<String, *>) =
        WeaviateObject.builder()
            .className(DOCUMENT_CLASS)
            .properties(properties)
            .build()
}
