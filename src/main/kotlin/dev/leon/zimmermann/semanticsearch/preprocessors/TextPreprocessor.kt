package dev.leon.zimmermann.semanticsearch.preprocessors

interface TextPreprocessor {
    fun preprocess(texts: Array<String>): Array<String>
}
