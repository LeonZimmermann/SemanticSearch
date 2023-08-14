package dev.leon.zimmermann.semanticsearch.preprocessors.impl

import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor

class IdentityTextPreprocessor: TextPreprocessor {
    override fun preprocess(input: String): String {
        return input
    }
}
