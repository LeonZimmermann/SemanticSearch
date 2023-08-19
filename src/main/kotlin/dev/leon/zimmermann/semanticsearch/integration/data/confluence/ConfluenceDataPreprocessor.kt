package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.TITLE_TAG
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ConfluenceDataPreprocessor(private val textPreprocessor: TextPreprocessor) {

    companion object {
        private val IGNORE_PREPROCESS = arrayOf(TITLE_TAG)
        private val MAIN_CONTENT_ID = "main-content"
    }

    fun apply(data: String): Map<String, *>? {
        val document = Jsoup.parse(data)
        if (document.getElementById(MAIN_CONTENT_ID) == null) {
            return null
        }
        return document
            .let { extractDataFromHtml(it) }
            .mapValues { it.value.joinToString(" ").replace("\\s+".toRegex(), " ") }
            .mapValues { applyPreprocessing(it.key, it.value) }
    }

    private fun applyPreprocessing(tag: String, string: String) =
        if (!IGNORE_PREPROCESS.contains(tag)) textPreprocessor.preprocess(string) else string

    private fun extractDataFromHtml(element: Element): Map<String, Array<String>> {
        return mapOf(
            TITLE_TAG to extract(element, TITLE_TAG),
            H1_TAG to extract(element.getElementById(MAIN_CONTENT_ID), H1_TAG),
            H2_TAG to extract(element.getElementById(MAIN_CONTENT_ID), H2_TAG),
            PARAGRAPH_TAG to extract(element.getElementById(MAIN_CONTENT_ID), PARAGRAPH_TAG),
        )
    }

    private fun extract(element: Element, tagName: String): Array<String> {
        return element.getElementsByTag(tagName).map {
            it.text().lowercase().replace("\\s+".toRegex(), " ")
        }.toTypedArray()
    }
}
