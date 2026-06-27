package org.sdet.testing.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductListSchemaTest {
    static RequestSpecification orders=new RequestSpecBuilder()
            .setBaseUri("http://localhost:4000")
            .setBasePath("/api/products")
            .build();
    @Test
    @DisplayName("Product List Matches Schema")
    void proudctListMatchesSchema()
    {
        given()
                .spec(orders)
                .when().get("")
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/json/product-list.schema.json"));


    }
}
