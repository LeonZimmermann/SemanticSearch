package dev.leon.zimmermann.semanticsearch.preprocessors.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DefaultTextPreprocessorTest {

    private val testee = DefaultTextPreprocessor("/stop_words_german.txt")

    @Test
    fun testPreprocess() {
        assertEquals("da test", testee.preprocess("Das ist ein Test"))
        assertEquals("da test er mehrer stze", testee.preprocess("Das ist ein Test. Er hat mehrere Sätze."))
    }
}
