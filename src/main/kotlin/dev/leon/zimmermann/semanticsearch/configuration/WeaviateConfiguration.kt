package dev.leon.zimmermann.semanticsearch.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "weaviate")
data class WeaviateConfiguration(var hostname: String = "", var port: String = "")
