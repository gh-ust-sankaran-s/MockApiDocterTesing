package org.sdet.testing.app.dbFramework.week2_assess.test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import org.sdet.testing.app.dbFramework.week2_assess.config.week2_db_config;
import org.sdet.testing.app.dbFramework.week2_assess.model.week2_order_row;
import org.sdet.testing.app.dbFramework.week2_assess.support.week2_db_support;
import org.sdet.testing.app.dbFramework.week2_assess.support.api_spec_support;
import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.reqSpec.requestSpec;
import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.respSpec.responseSpec;
import org.sdet.testing.app.specFactory;
import org.sdet.testing.app.test.dbFramework.test.reqSpec;

public class week2_order_test {
    private static final String BASE_URL = "http://localhost:4000";
    private static String token;
    private static week2_db_support db;
    private final List<Long> created_ids = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {
        token = api_spec_support.AuthOrder();
        assertNotNull(token, "Auth token should be generated before tests run");
        week2_db_config conf = week2_db_config.fromEnv();
        db = new week2_db_support(conf);
        assertTrue(db.isReachable());
    }

    private RequestSpecification authSpec() {
        return requestSpec.secureOrderSpec(token);
    }

    @Test
    @DisplayName("no token returns 401")
    void no_token_returns_401() {
        given().baseUri(BASE_URL)
                .when().get("/api/secure/orders/1")
                .then().spec(responseSpec.unauth401());
    }

    @Test
    @DisplayName("invalid token returns 401")
    void invalid_token_returns_401() {
        given().baseUri(BASE_URL)
                .header("Authorization", "Bearer bad-token")
                .when().get("/api/secure/orders/1")
                .then().spec(responseSpec.unauth401());
    }

    @Test
    @DisplayName("forbidden role returns 403")
    void forbidden_role_returns_403() {
        given().baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .auth().oauth2("demo-token-1-customer")
                .body(Map.of("items", List.of(101, 107)))
                .when().post("/api/secure/orders")
                .then().spec(responseSpec.forbidn403());
    }
    @Test
    @DisplayName("Calling Allocate with Invalid")
    void Calling_Allocat_with_InvalidorBeforeCreated(){
        given().spec(authSpec())
                .when().post("/{id}/allocate",4987)
                .then().spec(responseSpec.validate404());
    }
    @Test
    @DisplayName("allocate after shipped returns 409")
    void allocate_after_shipped_returns_409() throws Exception {
        Response create = given()
                .spec(authSpec())
                .body(Map.of("items", List.of(101, 107)))
                .when().post("")
                .then().statusCode(201)
                .body("orderId", notNullValue())
                .extract().response();
        long orderId = ((Number) create.path("orderId")).longValue();
        assertTrue(orderId > 0, "Created orderId should be a positive number");
        created_ids.add(orderId);

        given()
                .spec(authSpec())
                .when().post("/{id}/allocate", orderId)
                .then().statusCode(200);
        given()
                .spec(authSpec())
                .when().post("/{id}/ship", orderId)
                .then().statusCode(200);
        given()
                .spec(authSpec())
                .when().post("/{id}/allocate", orderId)
                .then().spec(responseSpec.validate409());
    }
    @Test
    @DisplayName("M3: product detail matches product JSON schema")
    void schema_validation(){
        given()
                .spec(requestSpec.legacyPro())
                .when()
                .get("/101.xml")
                .then()
                .body(matchesXsdInClasspath("schemas/xsd/product.xsd"));
    }
   
}
