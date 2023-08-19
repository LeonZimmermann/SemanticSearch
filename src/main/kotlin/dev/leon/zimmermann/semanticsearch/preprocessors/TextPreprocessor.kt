package dev.leon.zimmermann.semanticsearch.preprocessors

interface TextPreprocessor {
    fun preprocess(input: String): String
}
