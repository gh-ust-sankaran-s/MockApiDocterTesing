package org.sdet.testing.app;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.*;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class schemaValidationTest {
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            System.getenv().getOrDefault("BASE_URL","http://localhost:4000")
    );
    @BeforeAll
    static void setup(){
        RestAssured.baseURI= BASE_URL;
        RestAssured.basePath= "api/";
    }
    static  RequestSpecification products_read(){

        return readspec("/api/products/101.xml");
    }

    static RequestSpecification readspec(String basepath) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath(basepath)
                .build().accept("Accept");
    }
    static RequestSpecification legacyPro (){
        return new RequestSpecBuilder()
        .setBaseUri("http://localhost:4000")
                .setBasePath("/api/legacy/products")
                .setContentType(ContentType.XML)
                .build();
    }

    @Test
    @DisplayName("M2: product detail matches product JSON schema")
    void productDetail_matches(){
        given()
                .spec(products_read())
                .when()
                .get("/{id}",101)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/json/product.schema.json"));
    }
    @Test
    @DisplayName("M3: product detail matches product JSON schema")
    void schema_validation(){
        given()
                .spec(legacyPro())
                .when()
                .get("/101.xml")
                .then()
                .body(matchesXsdInClasspath("schemas/xsd/product.xsd"));
    }
}
