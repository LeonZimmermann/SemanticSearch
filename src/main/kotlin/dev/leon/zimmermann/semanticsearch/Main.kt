package dev.leon.zimmermann.semanticsearch

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@AutoConfiguration
class SemanticSearchApplication

fun main(args: Array<String>) {
    runApplication<SemanticSearchApplication>(*args)
}
