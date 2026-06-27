package com.ust.sdet.api;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
public class wiremockServiceVirtualizationTest {
    
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @BeforeEach
    void poinConsumerAtWireMock() {
        io.restassured.RestAssured.baseURI = wm.baseUrl();
    }
    @Test
    public void stubInventory() {
        wm.stubFor(get(urlPathEqualTo("/get/inventry/SKU-9"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sku\":\"SKU-9\",\"qty\":5}")));

        given()
                .when()
                .get("/get/inventry/SKU-9")
                .then()
                .statusCode(200)
                .body("sku", equalTo("SKU-9"))
                .body("qty", equalTo(5));
    }
    @Test
    public void stubInventory1() {
        wm.stubFor(get(urlPathEqualTo("/get/inventry/SKU-0"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"OUT_OF_STOCK\"}")));

        given()
                .when()
                .get("/get/inventry/SKU-0")
                .then()
                .statusCode(409)
                .body("error", equalTo("OUT_OF_STOCK"));
    }
    @Test
    public void stubInventory2() throws Exception {
        wm.stubFor(get(urlPathEqualTo("/get/orders/slow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(3000)
                        .withBody("{\"status\":\"delayed\"}")));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestShortTimeout = HttpRequest.newBuilder()
                .uri(URI.create(wm.baseUrl() + "/get/orders/slow"))
                .timeout(Duration.ofSeconds(1))
                .GET()
                .build();

        assertThrows(HttpTimeoutException.class, () ->
                client.send(requestShortTimeout, HttpResponse.BodyHandlers.ofString())
        );

        HttpRequest requestLongTimeout = HttpRequest.newBuilder()
                .uri(URI.create(wm.baseUrl() + "/get/orders/slow"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestLongTimeout, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"delayed\"}", response.body());
    }
     @Test
    public void stubInventory3() {
        wm.stubFor(get(urlPathEqualTo("/get/orders/42"))
                .inScenario("FULFILLMENT")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PENDING\"}"))
                  .willSetStateTo("CONFIRMED"));

        given()
                .when()
                .get("/get/orders/42")
                .then()
                .statusCode(200)
                .body("status", equalTo("PENDING"));
         wm.stubFor(get(urlPathEqualTo("/get/orders/42"))
         .inScenario("FULFILLMENT")
                .whenScenarioStateIs("CONFIRMED")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\"}")));
        given()
                .when()
                .get("/get/orders/42")
                .then()
                .statusCode(200)
                .body("status", equalTo("CONFIRMED"));
    }


}
