package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import com.google.gson.internal.LinkedTreeMap
import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.integration.data.DataServiceHelper
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(javaClass.toString())

    private val dataServiceHelper = DataServiceHelper()
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
            ?.peek { logger.debug("reading file ${it.name}") }
            ?.map { it.toURI().path to it.readText(Charset.defaultCharset()) }
            ?.map { it.first to confluenceDataPreprocessor.apply(it.second) }
            ?.map { addUrlToProperties(it) }
            ?.peek { logger.debug("data: $it") }
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
                dataServiceHelper.buildProperties(
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

    override fun parseQueryResult(
        queryResult: Any,
        parseAdditionals: (LinkedTreeMap<String, Any>) -> Map<String, String>
    ): Array<Map<String, String>> {
        return ((queryResult as LinkedTreeMap<*, *>)["Get"] as LinkedTreeMap<*, List<*>>)[getDatabaseScheme().className]
            .orEmpty()
            .map { it as LinkedTreeMap<String, String> }
            .map {
                val result = getMapOfData(it)
                val additionalMap =
                    (it["_additional"] as? LinkedTreeMap<String, Any>)?.let(parseAdditionals)
                if (additionalMap != null) {
                    result.putAll(additionalMap)
                }
                result
            }
            .toTypedArray()
    }

    private fun getMapOfData(sourceMap: LinkedTreeMap<String, String>) =
        mutableMapOf(
            "id" to (sourceMap["id"] ?: ""),
            "documentUrl" to (sourceMap["documentUrl"] ?: ""),
            "title" to (sourceMap["title"] ?: ""),
            "h1" to (sourceMap["h1"] ?: ""),
            "h2" to (sourceMap["h2"] ?: ""),
            "p" to (sourceMap["p"] ?: ""),
        )

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
