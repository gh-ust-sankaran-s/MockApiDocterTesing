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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Assignment_2 {

        private static final String BASE_URL = "http://localhost:4000";


        private static String token;
        private static RequestSpecification authed;

        @BeforeAll
        static void fetch() {
            token = specFactory.AuthOrder();
        }
        static RequestSpecification setupAuth(){
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
                    .post("")
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
                    .get("/{id}",5001)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", equalTo(5001))
                    .body("status", equalTo("Confirmed"));
        }
    @Test
    @DisplayName("With no token returns 401")
    void noToken() {

        given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/secure/orders/{id}", 5001)
                .then()
                .spec(specFactory_Assignment_2.unauthorized401())
                .log().all();
    }

    @Test
    @DisplayName("Invalid token returns 401")
    void invalidToken() {

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer invalid-token")
                .when()
                .get("/api/secure/orders/{id}", 6024)
                .then()
                .spec(specFactory_Assignment_2.unauthorized401());
    }

    @Test
    @DisplayName("Viewer role returns 403")
    void forbiddenRole() {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .auth()
                .oauth2("demo-token-1-customer")
                .body(Map.of(
                        "items", List.of(101, 107)
                ))
                .when()
                .post("/api/secure/orders")
                .then()
                .spec(specFactory_Assignment_2.forbidden());
    }

    @Test
    @DisplayName("get product with  API key")
    void Test4() {
        given()
                .spec(specFactory_Assignment_2.fetchwithAPIkey(specFactory_Assignment_2.apiReturn()))
                .when().get("/{id}", 5001)
                .then()
                .body("partner",equalTo("UST Partner Channel"))
                .body("order.status",equalTo("Confirmed"))
                .body("order.items[0].product.category",equalTo("Footwear"))
                .statusCode(200);

        given()
                .spec(specFactory_Assignment_2.noAPIkey())
                .when().get("/{id}", 5001)
                .then()
                .body("message",equalTo("API key required"))
                .statusCode(401);

        given()
                .spec(specFactory_Assignment_2.fetchwithAPIkey("key"))
                .when().get("/{id}", 5001)
                .then()
                .body("message",equalTo("Invalid API key"))
                .statusCode(403);
    }
    }


