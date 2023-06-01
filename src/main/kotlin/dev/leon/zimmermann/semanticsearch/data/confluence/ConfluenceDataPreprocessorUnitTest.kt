package dev.leon.zimmermann.semanticsearch.data.confluence

import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.preprocessing.impl.DefaultTextPreprocessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

internal class ConfluenceDataPreprocessorUnitTest {

    @Test
    fun apply() {
        val textPreprocessor = DefaultTextPreprocessor("/stop_words_german.txt")
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)
        val input = javaClass.getResource("/test.html").readText(Charset.defaultCharset())
        val result = confluenceDataPreprocessor.apply(input)
        // TODO Fix
        assertEquals(mapOf(
            H1_TAG to arrayOf("testtitel"),
            H2_TAG to arrayOf("weiter", "testtitel", "h", "2"),
            PARAGRAPH_TAG to arrayOf(
                arrayOf("testparagraph", "extrahiert"),
                arrayOf("weiter", "paragraph", "h", "2", "tag", "finden", "erkannt")
            )), result)

    }
}
