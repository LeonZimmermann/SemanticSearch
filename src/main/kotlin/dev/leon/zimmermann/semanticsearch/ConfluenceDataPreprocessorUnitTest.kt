package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.ConfluenceDataService.Companion.PARAGRAPH_TAG
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

internal class ConfluenceDataPreprocessorUnitTest {

    @Test
    fun apply() {
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor("/stop_words_german.txt")
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
