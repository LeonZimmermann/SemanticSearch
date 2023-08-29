package dev.leon.zimmermann.semanticsearch.preprocessors.impl

import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import opennlp.tools.stemmer.PorterStemmer
import opennlp.tools.tokenize.SimpleTokenizer
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class DefaultTextPreprocessor(stopwordsFile: String) : TextPreprocessor {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val stopwords: List<String> =
        javaClass.getResource(stopwordsFile)?.readText(Charsets.UTF_8)?.split("\n")
            ?.map { it.trim() }
            ?.toList()
            ?: throw RuntimeException("Could not find stopwords file (\"$stopwordsFile\")")
    private val porterStemmer = PorterStemmer()

    override fun preprocess(input: String): String {
        return input
            .let { removePunctuation(it) }
            .let { removeStopwords(it) }
            .let { tokenizeAndStem(it) }
            .joinToString(" ")
            .lowercase()
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
            .filter { !it.isNullOrEmpty() }
            .mapNotNull {
                try {
                    porterStemmer.stem(it)
                } catch (exception: ArrayIndexOutOfBoundsException) {
                    logger.error("Error stemming token \"$it\"")
                    null
                }
            }
            .distinct()
            .toList()
}
