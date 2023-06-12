package dev.leon.zimmermann.semanticsearch.data.confluence

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import java.util.*

class ConfluenceDataPreprocessor(private val textPreprocessor: TextPreprocessor) {

    companion object {
        private const val PARAGRAPH_PATTERN = "<p.*?>(.*?)</p>"
        private const val H1_PATTERN = "<h1.*?>(.*?)</h1>"
        private const val H2_PATTERN = "<h2.*?>(.*?)</h2>"
    }

    fun apply(data: String): Map<String, *> {
        return data.replace("[\\n\\r]".toRegex(), "")
            .lowercase(Locale.getDefault())
            .let { extractDataFromHtml(it) }
            .mapValues { textPreprocessor.preprocess(it.value).joinToString(" ") }
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
            .map { it.replace("<.*?>".toRegex(), "") }
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .toList()
            .toTypedArray()
    }
}
