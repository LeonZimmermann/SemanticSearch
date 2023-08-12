package dev.leon.zimmermann.semanticsearch

import dev.leon.zimmermann.semanticsearch.integration.client.ClientManager
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.DefaultTextPreprocessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class SemanticSearchConfiguration : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**");
    }

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
        return ConfluenceDataService("data/sites", textPreprocessor())
    }
}
