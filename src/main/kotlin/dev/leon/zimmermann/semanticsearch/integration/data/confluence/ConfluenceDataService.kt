package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import com.google.gson.internal.LinkedTreeMap
import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.integration.data.DataServiceHelper
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.misc.model.BM25Config
import io.weaviate.client.v1.misc.model.InvertedIndexConfig
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asSequence


class ConfluenceDataService(private val pathToFolder: String, textPreprocessor: TextPreprocessor) :
    DataService {

    companion object {
        private const val VECTORIZER = "text2vec-transformers"
        private const val WEAVIATE_TEXT_DATATYPE = "text"

        const val DOCUMENT_CLASS = "Document"

        const val ID = "id"
        const val DOCUMENT_URL = "documentUrl"
        const val TITLE_TAG = "title"
        const val H1_TAG = "h1"
        const val H2_TAG = "h2"
        const val PARAGRAPH_TAG = "p"
    }

    private val logger = LoggerFactory.getLogger(javaClass.toString())

    private val dataServiceHelper = DataServiceHelper()
    private val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)

    override fun getData(): Stream<WeaviateObject> {
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
            ?.peek { logger.debug("reading file ${it.name} on Thread ${Thread.currentThread().name}") }
            ?.map { it.toURI().path to it.readText(Charset.defaultCharset()) }
            ?.map { it.first to confluenceDataPreprocessor.apply(it.second) }
            ?.filter { it.second != null }
            ?.map { it.first to it.second!! }
            ?.map { addUrlToProperties(it) }
            ?.map { createWeaviateObject(it) }
            ?: throw IOException("Could not get files from directory (\"$pathToFolder\")")
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
            ).vectorIndexConfig(
                VectorIndexConfig.builder()
                    .distance("l2-squared")
                    .ef(100)
                    .efConstruction(128)
                    .build()
            )
            .invertedIndexConfig(
                InvertedIndexConfig.builder()
                    .bm25(
                        BM25Config.builder()
                            .b(.5f)
                            .k1(.5f)
                            .build()
                    )
                    .build()
            )
            .vectorizer(VECTORIZER)
            .build()
    }

    override fun getMapOfData(sourceMap: LinkedTreeMap<String, String>): Map<String, String> = mapOf(
        ID to (sourceMap[ID] ?: "").toString(),
        DOCUMENT_URL to (sourceMap[DOCUMENT_URL] ?: "").toString(),
        TITLE_TAG to (sourceMap[TITLE_TAG] ?: "").toString(),
        H1_TAG to (sourceMap[H1_TAG] ?: "").toString(),
        H2_TAG to (sourceMap[H2_TAG] ?: "").toString(),
        PARAGRAPH_TAG to (sourceMap[PARAGRAPH_TAG] ?: "").toString(),
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
