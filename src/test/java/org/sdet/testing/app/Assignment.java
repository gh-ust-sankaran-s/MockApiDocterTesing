package org.sdet.testing.app;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.specification.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assignment {
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            System.getenv().getOrDefault("BASE_URL","http://localhost:4000")
    );
    @BeforeAll
    static void setup(){
        RestAssured.baseURI= BASE_URL;
        RestAssured.basePath= "api/";
    }
    static RequestSpecification loginJson=new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setBasePath("/api/login")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
    static RequestSpecification commonJson=new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
    static ResponseSpecification LoginRes=new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();
    static RequestSpecification prodJson=new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .build();
    static RequestSpecification addToCart=new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setBasePath("/api/cart/items")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();

    @Test
    @DisplayName("Assignment- Login to order")
    void Assignment_1()
    {
        String token =given()
                .spec(loginJson)
                .body(Map.of(
                        "email", "customer@example.com",
                        "password", "Password@123"
                ))
                .when()
                .post("")
                .then()
                .log().all()
                .spec(LoginRes)
                .extract()
                .path("token");
        given()
                .spec(prodJson)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/cart")
                .then()
                .log().all()
                .statusCode(204);
        var orderRecord=Map.of(
                "productId", 101,
                "quantity", 1,
                "size", "UK 7",
                "color", "Navy",
                "fulfilment", "Home delivery",
                "address", "1/22, Rajapalayam");
        given()
                .spec(addToCart)
                .header("Authorization", "Bearer " + token)
                .body(orderRecord)
                .when()
                .post("")
                .then()
                .log().all()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("productId", equalTo(101))
                .body("quantity", equalTo(1))
                .body("color",equalTo("Navy"));
        String id=given()
                .spec(commonJson)
                .header("Authorization", "Bearer " + token)
                .body(orderRecord)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo("Confirmed"))
                .body("payment", equalTo("Paid"))
                .body("paymentMethod", equalTo("Credit card"))
                .body(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .extract().path("id").toString();
        given()
                .spec(commonJson)
                .header("Authorization", "Bearer " + token)
                .pathParam("id",id)
                .when()
                .get("/api/orders/{id}")
                .then()
                .log().all()
                .spec(LoginRes)
                .body("id.toString()",equalTo(id));


    }


}
