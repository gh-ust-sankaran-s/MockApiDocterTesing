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
import org.sdet.testing.app.dbFramework.week2_assess.support.api_spec_support;

import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.reqSpec.requestSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.respSpec.responseSpec;
import org.sdet.testing.app.test.dbFramework.test.reqSpec;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class edgeCases {
    private static final String BASE_URL = "http://localhost:4000";
    private static String token;
    private final List<Long> created_ids = new ArrayList<>();

    @BeforeAll
    void setup() {
        token = api_spec_support.AuthOrder();
        assertNotNull(token, "Auth token should be generated before edge case tests run");
    }
    private RequestSpecification authSpec() {
        return requestSpec.secureOrderSpec(token);
    }

    @Test
    @DisplayName("order missing")
    void orderMissing() {
        given()
                .spec(authSpec())
                .when().get("/{id}", 999999)
                .then()
                .spec(responseSpec.orderNotFn404());
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