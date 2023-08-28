package dev.leon.zimmermann.semanticsearch.integration.api

import io.restassured.RestAssured.*
import io.restassured.matcher.RestAssuredMatchers.*
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.hamcrest.Matchers.*
import org.json.JSONArray
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
internal class SearchPerformanceEvaluationIntegrationTest {

    @LocalServerPort
    private lateinit var port: String

    @Test
    fun evaluateSearchPerformance() {
        val resultFile = getResultFile()
        resultFile.writer().use { resultWriter ->
            initializeDatabase()
            val inputFile = getInputFile()
            resultWriter.appendLine("Anwendungsfall;Bereich;Sucheingabe;Erwartetes Dokument;Gefundene Dokumente;Hit;Hit in Five;Hit in Three; Hit in One")
            inputFile.readLines()
                .filterIndexed { index, _ -> index > 0 }
                .map { it.split(";") }
                .forEach { inputs ->
                    val usecase = inputs[0]
                    val region = inputs[1]
                    val expectedDocument = inputs[2]
                    val searchInputs = inputs[3]
                    searchInputs.split(",").map { it.trim() }.forEach { search ->
                        val response = makeSearchRequest(search)
                        val titleList = getTitleListFromResponse(response)
                        val titleList5 = titleList.subList(0, 5)
                        val titleList3 = titleList.subList(0, 3)
                        val firstTitle = titleList.first()
                        val hit = titleList.any { it.trim().startsWith(expectedDocument, true) }
                        val hit5 = titleList5.any { it.trim().startsWith(expectedDocument, true) }
                        val hit3 = titleList3.any { it.trim().startsWith(expectedDocument, true) }
                        val hit1 = firstTitle.trim().startsWith(expectedDocument, true)
                        resultWriter.appendLine(
                            "$usecase;$region;$search;$expectedDocument;${
                                titleList.joinToString(
                                    ","
                                )
                            };$hit;$hit5;$hit3;$hit1"
                        )
                    }
                }
        }
    }

    private fun getResultFile(): File {
        val file = File("result.csv")
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw IOException("File could not be created")
            }
        }
        return file
    }

    private fun getInputFile(): File {
        val inputFilePath = this.javaClass.getResource("/comparison.csv")?.file
            ?: throw RuntimeException("No input file comparison.csv")
        val inputFile = File(inputFilePath)
        if (!inputFile.exists()) {
            throw RuntimeException("No input file comparison.csv")
        }
        return inputFile
    }

    private fun initializeDatabase() {
        post("http://localhost:$port/initialize")
            .then()
            .statusCode(200)
    }

    private fun makeSearchRequest(searchInput: String): CloseableHttpResponse {
        val post = HttpPost("http://localhost:$port/search")
        post.entity = StringEntity(searchInput)
        return HttpClientBuilder.create().build().execute(post)
    }

    private fun getTitleListFromResponse(response: CloseableHttpResponse): List<String> {
        val responseArray = JSONArray(
            BufferedReader(InputStreamReader(response.entity.content)).readLines()
                .joinToString()
        )
        return (0 until responseArray.length()).map { i ->
            responseArray.getJSONObject(i).getString("title")
        }
    }
}
