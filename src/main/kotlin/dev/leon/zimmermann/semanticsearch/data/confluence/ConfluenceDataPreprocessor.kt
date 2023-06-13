package dev.leon.zimmermann.semanticsearch.data.confluence

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.TITLE_TAG
import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import java.util.*

class ConfluenceDataPreprocessor(private val textPreprocessor: TextPreprocessor) {

    companion object {
        private const val TITLE_PATTERN = "<title.*?>(.*?)</title>"
        private const val H1_PATTERN = "<h1.*?>(.*?)</h1>"
        private const val H2_PATTERN = "<h2.*?>(.*?)</h2>"
        private const val PARAGRAPH_PATTERN = "<p.*?>(.*?)</p>"

        private val IGNORE_PREPROCESS = arrayOf(TITLE_TAG)
    }

    fun apply(data: String): Map<String, *> {
        return data.replace("[\\n\\r]".toRegex(), "")
            .lowercase()
            .let { extractDataFromHtml(it) }
            .mapValues {
                if (!IGNORE_PREPROCESS.contains(it.key)) {
                    textPreprocessor.preprocess(it.value).joinToString(" ")
                } else {
                    it.value.joinToString(" ")
                }
            }
    }

    private fun extractDataFromHtml(html: String): Map<String, Array<String>> {
        return mapOf(
            TITLE_TAG to extractTitle(html),
            H1_TAG to extract(H1_PATTERN, html),
            H2_TAG to extract(H2_PATTERN, html),
            PARAGRAPH_TAG to extract(PARAGRAPH_PATTERN, html),
        )
    }

    private fun extractTitle(html: String): Array<String> {
        return extract(TITLE_PATTERN, html)
            .map { it.split(":").last() }
            .map { it.trim() }
            .toTypedArray()
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
