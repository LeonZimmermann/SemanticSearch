package dev.leon.zimmermann.semanticsearch.preprocessors.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IdentityTextPreprocessorUnitTest {

    private val testee = IdentityTextPreprocessor()

    @Test
    fun testPreprocess() {
        assertEquals("Irgendetwas", testee.preprocess("Irgendetwas"))
    }
}
