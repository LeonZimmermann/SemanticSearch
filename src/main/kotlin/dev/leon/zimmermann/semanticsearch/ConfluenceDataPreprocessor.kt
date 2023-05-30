package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.PARAGRAPH_TAG
import opennlp.tools.stemmer.PorterStemmer
import opennlp.tools.tokenize.SimpleTokenizer
import java.nio.charset.Charset
import java.util.*
import java.util.stream.Collectors

class ConfluenceDataPreprocessor(stopwordsFile: String) {

    companion object {
        private const val PARAGRAPH_PATTERN = "<p.*?>(.*?)</p>"
        private const val H1_PATTERN = "<h1.*?>(.*?)</h1>"
        private const val H2_PATTERN = "<h2.*?>(.*?)</h2>"
    }

    private val stopwords: List<String> =
        javaClass.getResource(stopwordsFile)?.readText(Charset.defaultCharset())?.split("\n")
            ?.map { it.trim() }
            ?.toList()
            ?: throw RuntimeException("Could not find stopwords file (\"$stopwordsFile\")")
    private val porterStemmer = PorterStemmer()

    fun apply(list: List<String>): List<Map<String, Array<Array<String>>>> {
        return list
            .map { it.replace("[\\n\\r]".toRegex(), "") }
            .map { it.lowercase(Locale.getDefault()) }
            .map { extractDataFromHtml(it) }
            .map { it.mapValues { entry -> preprocess(entry.value) } }
    }

    private fun extractDataFromHtml(html: String): Map<String, Array<String>> {
        return mapOf(
            H1_TAG to extract(H1_PATTERN, html),
            H2_TAG to extract(H2_PATTERN, html),
            PARAGRAPH_TAG to extract(PARAGRAPH_PATTERN, html),
        )
    }

    private fun extract(pattern: String, html: String): Array<String> {
        return pattern.toRegex()
            .findAll(html)
            .map { it.groupValues[1] }
            .toList()
            .toTypedArray()
    }

    private fun preprocess(texts: Array<String>): Array<Array<String>> {
        return texts
            .map { removePunctuation(it) }
            .map { removeStopwords(it) }
            .map { text -> tokenize(text) }
            .toTypedArray()
    }

    private fun removePunctuation(text: String): String {
        val punctuationRegex: Regex = "[^\\w\\s]".toRegex()
        return text.replace(punctuationRegex, "")
    }

    private fun removeStopwords(text: String): String {
        val stopwordsRegex: Regex = stopwords.stream()
            .collect(Collectors.joining("|", "\\b(", ")\\b\\s?")).toRegex()
        return text.replace(stopwordsRegex, "")
    }

    private fun tokenize(text: String) =
        SimpleTokenizer.INSTANCE.tokenize(text).map { porterStemmer.stem(it) }
            .distinct()
            .toTypedArray()
}
