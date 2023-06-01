package dev.leon.zimmermann.semanticsearch.preprocessing

interface TextPreprocessor {
    fun preprocess(texts: Array<String>): Array<String>
}
