package dev.leon.zimmermann.semanticsearch.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "search")
data class SearchConfiguration(var preprocess: Boolean = false, var alpha: Float = 0f)
