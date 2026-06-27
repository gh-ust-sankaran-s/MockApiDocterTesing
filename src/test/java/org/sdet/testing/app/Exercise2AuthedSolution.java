package org.sdet.testing.app;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
public class Exercise2AuthedSolution {
        private static final String BASE_URL = "http://localhost:4000";

        private static String token;
        private static RequestSpecification authed;
    @BeforeAll
    static void fetch() {
        token = specFactory.AuthOrder(); // call fetch method
    }
    public static RequestSpecification setupAuth(){
        return new RequestSpecBuilder()
                            .setBaseUri(BASE_URL)
                            .setBasePath("/api/secure/orders")
                            .setContentType(ContentType.JSON)
                            .setAccept(ContentType.JSON)
                            .setAuth(RestAssured.oauth2(token))
                            .build();
        }

        @Test
        @DisplayName("Authenticate, create secure order and validate schema")
        void authenticateThenCreateOrderAndValidateContract() {

            given()
                    .spec(setupAuth())
                    .body(
                            Map.of(
                                    "items", List.of(101, 107)
                            )
                    )
                    .when()
                    .post("/api/secure/orders")
                    .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("orderId", notNullValue())
                    .body("status", equalTo("CREATED"))
                    .body("items.size()", equalTo(2))
                    .body(matchesJsonSchemaInClasspath(
                            "schemas/json/order.schema.json"
                    ));
        }

        @Test
        @DisplayName("Read secure order using same authed spec")
        void readSecureOrder() {

            given()
                    .spec(setupAuth())
                    .when()
                    .get("/api/secure/orders/5001")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", equalTo(5001))
                    .body("status", equalTo("Confirmed"));
        }
    }
