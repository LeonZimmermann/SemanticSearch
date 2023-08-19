package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.DefaultTextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.IdentityTextPreprocessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

internal class ConfluenceDataPreprocessorUnitTest {

    @Test
    fun apply() {
        val textPreprocessor = IdentityTextPreprocessor()
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)
        val input = javaClass.getResource("/test.html").readText(Charset.defaultCharset())
        val result = confluenceDataPreprocessor.apply(input)
        assertEquals("test-titel", result[H1_TAG])
        assertEquals("noch ein weiterer test-titel, aber als h2", result[H2_TAG])
        assertEquals("das ist ein erster test-paragraph, welcher extrahiert werden sollte das ist ein weiterer paragraph. dieser paragraph ist unter einem h2-tag zu finden, sollte aber auch erkannt werden", result[PARAGRAPH_TAG])
    }
}
