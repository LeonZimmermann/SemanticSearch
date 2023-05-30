package dev.leon.zimmermann.semanticsearch

import io.weaviate.client.v1.data.model.WeaviateObject
import opennlp.tools.stemmer.PorterStemmer
import opennlp.tools.tokenize.SimpleTokenizer
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.stream.Collectors


class ConfluenceDataService(private val pathToFolder: String, stopwordsFile: String) : DataService {

    companion object {
        const val DOCUMENT_CLASS = "Document"

        const val PARAGRAPH_TAG = "p"
        const val H1_TAG = "h1"
        const val H2_TAG = "h2"
    }

    private val confluenceDataPreprocessor = ConfluenceDataPreprocessor(stopwordsFile)

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
            ?.map { it.readText(Charset.defaultCharset()) }
            ?.let { confluenceDataPreprocessor.apply(it) }
            ?.map { createWeaviateObject(it) }
            ?.toTypedArray()
            ?: throw RuntimeException("Could not get files from directory (\"$pathToFolder\")")
    }

    private fun createWeaviateObject(it: Map<String, Array<Array<String>>>) =
        WeaviateObject.builder()
            .className(DOCUMENT_CLASS)
            .properties(it)
            .build()
}
