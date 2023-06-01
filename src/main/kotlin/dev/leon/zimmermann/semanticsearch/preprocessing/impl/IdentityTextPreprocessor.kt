package dev.leon.zimmermann.semanticsearch.preprocessing.impl

import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor

class IdentityTextPreprocessor: TextPreprocessor {
    override fun preprocess(texts: Array<String>): Array<String> {
        return texts
    }
}
