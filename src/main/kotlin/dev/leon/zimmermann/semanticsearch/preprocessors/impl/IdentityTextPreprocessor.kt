package dev.leon.zimmermann.semanticsearch.preprocessors.impl

import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor

class IdentityTextPreprocessor: TextPreprocessor {
    override fun preprocess(texts: Array<String>): Array<String> {
        return texts
    }
}
