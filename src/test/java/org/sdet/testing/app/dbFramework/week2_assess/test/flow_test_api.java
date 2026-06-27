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
import org.sdet.testing.app.dbFramework.week2_assess.config.week2_db_config;
import org.sdet.testing.app.dbFramework.week2_assess.support.api_spec_support;
import org.sdet.testing.app.dbFramework.week2_assess.support.week2_db_support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.reqSpec.requestSpec;
import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.respSpec.responseSpec;
import org.sdet.testing.app.test.dbFramework.test.reqSpec;

public class flow_test_api {
    private static final String BASE_URL = "http://localhost:4000";
    private static String token;
    private static week2_db_support db;
    private final List<Long> created_ids = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {
        token = api_spec_support.AuthOrder();
        week2_db_config conf = week2_db_config.fromEnv();
        db = new week2_db_support(conf);
        assertTrue(db.isReachable());
    }

    private RequestSpecification authSpec() {
        return requestSpec.secureOrderSpec(token);
    }
    @Test
    @DisplayName("Flow testing for api")
    void order_allocate_ship_api() throws Exception {
        Response create = given()
                .spec(authSpec())
                .body(Map.of("items", List.of(101, 107)))
                .when().post("")
                .then().statusCode(201)
                .body("orderId", notNullValue())
                .extract().response();
        long orderId = ((Number) create.path("orderId")).longValue();
        created_ids.add(orderId);
        given()
                .spec(authSpec())
                .when().post("/{id}/allocate", orderId)
                .then().spec(responseSpec.orderOk200WithSchema());

        Response allocated = given()
                .spec(authSpec())
                .when().get("/{id}", orderId)
                .then().spec(responseSpec.orderOk200WithSchema())
                .extract().response();

        assertEquals("ALLOCATED", allocated.path("status"));
        given()
                .spec(authSpec())
                .when().post("/{id}/ship", orderId)
                .then().spec(responseSpec.orderOk200WithSchema());
        Response shipped = given()
                .spec(authSpec())
                .when().get("/{id}", orderId)
                .then().spec(responseSpec.orderOk200WithSchema())
                .extract().response();
        assertEquals("SHIPPED", shipped.path("status"));
    }
    @AfterEach
    void cleanup() {
        for (Long id : created_ids) {
            given().spec(authSpec())
                    .when().delete("/{id}", id)
                    .then().statusCode(204);
        }
        created_ids.clear();
    }
}
