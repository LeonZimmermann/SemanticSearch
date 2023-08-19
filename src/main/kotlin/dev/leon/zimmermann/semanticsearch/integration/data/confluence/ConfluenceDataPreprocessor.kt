package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.TITLE_TAG
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ConfluenceDataPreprocessor(private val textPreprocessor: TextPreprocessor) {

    companion object {
        private val IGNORE_PREPROCESS = arrayOf(TITLE_TAG)
    }

    fun apply(data: String): Map<String, *> {
        return Jsoup.parse(data)
            .let { extractDataFromHtml(it) }
            .mapValues { it.value.joinToString(" ").replace("\\s+".toRegex(), " ") }
            .mapValues {
                if (!IGNORE_PREPROCESS.contains(it.key)) textPreprocessor.preprocess(it.value) else it.value
            }
    }

    private fun extractDataFromHtml(document: Document): Map<String, Array<String>> {
        return mapOf(
            TITLE_TAG to extract(document, "title"),
            H1_TAG to extract(document, "h1"),
            H2_TAG to extract(document, "h2"),
            PARAGRAPH_TAG to extract(document, "p"),
        )
    }

    private fun extract(document: Document, tagName: String): Array<String> {
        return document.getElementsByTag(tagName).map {
            it.html()
                .lowercase()
                .replace("\\s+".toRegex(), " ")
        }.toTypedArray()
    }
}
