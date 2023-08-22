package dev.leon.zimmermann.semanticsearch.integration.api

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
internal class SearchControllerIntegrationTest {

    private val logger = LoggerFactory.getLogger(javaClass)

    @LocalServerPort
    private lateinit var port: String

    private lateinit var baseUrl: String

    @BeforeAll
    fun setup() {
        baseUrl = "http://localhost:$port"
        val request = HttpPost("$baseUrl/initialize")
        val result = HttpClientBuilder.create()
            .build()
            .execute(request)
        logger.debug(result.toString())
        assertEquals(200, result.statusLine.statusCode)
    }

    @Test
    fun testSearch() {
        val request = HttpPost("$baseUrl/search")
            .apply { entity = StringEntity("vorgänge und aufträge") }
        val result = HttpClientBuilder.create()
            .build()
            .execute(request)
            .statusLine.statusCode
        assertEquals(200, result)
    }
}
