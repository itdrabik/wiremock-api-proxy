package com.example.wiremockapiproxy.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurrencyControllerTest {

    @LocalServerPort
    private int port;

    private static final int WIREMOCK_PORT = 8081;
    private WireMockServer wireMockServer;

    // Set the external API property to use WireMock
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("external.api.url", () -> "http://localhost:" + WIREMOCK_PORT);
    }

    @BeforeAll
    void startWireMock() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_PORT);
    }

    @BeforeEach
    void setupStubs() throws IOException {
        // Fake response for USD
        String usdJsonBody = readJsonFromFile("usdResponse.json");
        wireMockServer.stubFor(get(urlEqualTo("/api/exchangerates/rates/A/USD?format=json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(usdJsonBody)
                        .withStatus(200)));

        // Fake response for EUR
        String eurJsonBody = readJsonFromFile("eurResponse.json");
        wireMockServer.stubFor(get(urlEqualTo("/api/exchangerates/rates/A/EUR?format=json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(eurJsonBody)
                        .withStatus(200)));

        // 404 case for non-existent currency
        wireMockServer.stubFor(get(urlEqualTo("/api/exchangerates/rates/A/XYZ?format=json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Not Found\"}")));

        // Set up RestAssured to test against the local Spring Boot app
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    @AfterAll
    void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Test
    void testGetCurrencyRateUSD() {
        given()
                .queryParam("code", "USD")
                .when()
                .get("/currency")
                .then()
                .contentType("application/json")
                .statusCode(200)
                .body("code", equalTo("USD"))
                .body("rates[0].mid", equalTo(4.0321F));
    }

    @Test
    void testGetCurrencyRateEUR() {
        given()
                .queryParam("code", "EUR")
                .when()
                .get("/currency")
                .then()
                .contentType("application/json")
                .statusCode(200)
                .body("code", equalTo("EUR"))
                .body("rates[0].mid", equalTo(4.1898F));
    }

    @Test
    void testGetCurrencyRateNotFound() {
        given()
                .queryParam("code", "XYZ") // Currency that does not exist
                .when()
                .get("/currency")
                .then()
                .statusCode(404)
                .body("error", equalTo("Currency not found"));
    }


    private String readJsonFromFile(String fileName) throws IOException {
        Path path = new ClassPathResource("__files/" + fileName).getFile().toPath();
        return Files.readString(path);
    }
}
