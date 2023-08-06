package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.integration.client.ClientManager
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.DefaultTextPreprocessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SemanticSearchConfiguration {
    @Bean
    fun databaseClient(): DatabaseClient {
        return ClientManager()
    }

    @Bean
    fun textPreprocessor(): TextPreprocessor {
        return DefaultTextPreprocessor("/stop_words_german.txt")
    }

    @Bean
    fun dataService(): DataService {
        return ConfluenceDataService("mdk", textPreprocessor())
    }
}
