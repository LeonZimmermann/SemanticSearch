package dev.leon.zimmermann.semanticsearch.integration.api

import io.restassured.RestAssured.*
import io.restassured.matcher.RestAssuredMatchers.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
internal class SearchControllerIntegrationTest {

    @LocalServerPort
    private lateinit var port: String

    private lateinit var baseUrl: String

    @BeforeAll
    fun setup() {
        baseUrl = "http://localhost:$port"
        post("$baseUrl/initialize")
            .then()
            .statusCode(200)
    }

    @Test
    fun testSearch() {
        with()
            .body("vorgänge und aufträge")
            .`when`()
            .post("$baseUrl/search")
            .then()
            .statusCode(200)
            .assertThat()
            .body("title", containsInAnyOrder("vorgänge & aufträge - projekt mdk branchensoftware"))
    }
}
