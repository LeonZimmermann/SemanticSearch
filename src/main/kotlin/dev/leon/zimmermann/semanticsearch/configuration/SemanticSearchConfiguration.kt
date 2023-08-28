package dev.leon.zimmermann.semanticsearch.configuration

import dev.leon.zimmermann.semanticsearch.DataService
import dev.leon.zimmermann.semanticsearch.DatabaseClient
import dev.leon.zimmermann.semanticsearch.integration.client.ClientManager
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService
import dev.leon.zimmermann.semanticsearch.integration.data.tutorial.TutorialDataService
import dev.leon.zimmermann.semanticsearch.preprocessors.TextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.DefaultTextPreprocessor
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.IdentityTextPreprocessor
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
    fun databaseClient(weaviateConfiguration: WeaviateConfiguration): DatabaseClient {
        return ClientManager(weaviateConfiguration.hostname, weaviateConfiguration.port)
    }

    @Bean
    fun textPreprocessor(searchConfiguration: SearchConfiguration): TextPreprocessor {
        return if (searchConfiguration.preprocess) {
            DefaultTextPreprocessor("/stop_words_german.txt")
        } else {
            IdentityTextPreprocessor()
        }
    }

    @Bean
    fun dataService(
        dataConfiguration: DataConfiguration,
        searchConfiguration: SearchConfiguration
    ): DataService {
        return if (dataConfiguration.path.isEmpty()) {
            TutorialDataService()
        } else {
            ConfluenceDataService(dataConfiguration.path, textPreprocessor(searchConfiguration))
        }
    }
}
