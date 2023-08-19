package dev.leon.zimmermann.semanticsearch

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AlgorithmsKtTest {

    @Test
    fun testArrayAsBatches() {
        val inputArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val batchSize = 3
        val expectedResult = arrayOf(
            arrayOf(1, 2, 3),
            arrayOf(4, 5, 6),
            arrayOf(7, 8, 9)
        )
        assertTrue(expectedResult contentDeepEquals arrayAsBatches(inputArray, batchSize))
    }

    @Test
    fun testArrayAsBatchesWithAppendix() {
        val inputArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val batchSize = 3
        val expectedResult = arrayOf(
            arrayOf(1, 2, 3),
            arrayOf(4, 5, 6),
            arrayOf(7, 8)
        )
        assertTrue(expectedResult contentDeepEquals arrayAsBatches(inputArray, batchSize))
    }
}
