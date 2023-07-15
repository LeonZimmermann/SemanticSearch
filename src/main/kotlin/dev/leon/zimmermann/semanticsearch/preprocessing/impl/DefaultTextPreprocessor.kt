package dev.leon.zimmermann.semanticsearch.preprocessing.impl

import dev.leon.zimmermann.semanticsearch.preprocessing.TextPreprocessor
import opennlp.tools.stemmer.PorterStemmer
import opennlp.tools.tokenize.SimpleTokenizer
import java.nio.charset.Charset
import java.util.stream.Collectors

class DefaultTextPreprocessor(stopwordsFile: String): TextPreprocessor {

    private val stopwords: List<String> =
        javaClass.getResource(stopwordsFile)?.readText(Charset.defaultCharset())?.split("\n")
            ?.map { it.trim() }
            ?.toList()
            ?: throw RuntimeException("Could not find stopwords file (\"$stopwordsFile\")")
    private val porterStemmer = PorterStemmer()

    override fun preprocess(texts: Array<String>): Array<String> {
        return texts
            .map { removePunctuation(it) }
            .map { removeStopwords(it) }
            .flatMap { tokenizeAndStem(it) }
            .toTypedArray()
    }

    private fun removePunctuation(text: String): String {
        val punctuationRegex: Regex = "[^\\w\\s]".toRegex()
        return text.replace(punctuationRegex, "")
    }

    private fun removeStopwords(text: String): String {
        val stopwordsRegex: Regex = stopwords.stream()
            .collect(Collectors.joining("|", "\\b(", ")\\b\\s?")).toRegex()
        return text.replace(stopwordsRegex, "")
    }

    private fun tokenizeAndStem(text: String) =
        SimpleTokenizer.INSTANCE.tokenize(text)
            .map { porterStemmer.stem(it) }
            .distinct()
            .toList()
}
