package dev.leon.zimmermann.semanticsearch

data class Document(
    val id: String,
    val documentUrl: String,
    val titleTags: String,
    val h1Tags: String,
    val h2Tags: String,
    val pTags: String,
    val distance: Float
)
