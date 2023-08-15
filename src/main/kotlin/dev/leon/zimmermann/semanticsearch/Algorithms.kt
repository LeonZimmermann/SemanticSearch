package dev.leon.zimmermann.semanticsearch

inline fun <reified T> arrayAsBatches(array: Array<T>, batchSize: Int): Array<Array<T>> {
    return if (array.size < batchSize) {
        arrayOf(array)
    } else {
        val list = mutableListOf<Array<T>>()
        for (i in 0 until (array.size / batchSize)) {
            val currentStart = i * batchSize
            val currentEnd = (i + 1) * batchSize - 1
            list.add(array.sliceArray(IntRange(currentStart, currentEnd)))
        }
        if (array.size % batchSize != 0) {
            val lastStart = array.size - array.size % batchSize
            val lastEnd = array.size - 1
            list.add(array.sliceArray(IntRange(lastStart, lastEnd)))
        }
        list.toTypedArray()
    }
}
