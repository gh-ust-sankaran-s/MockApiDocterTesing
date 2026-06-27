package org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.reqSpec;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class requestSpec {
    private static final String BASE_URL = "http://localhost:4000";

    public static RequestSpecification secureOrderSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setAuth(RestAssured.oauth2(token))
                .build();
    }
    public static RequestSpecification legacyPro (){
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:4000")
                .setBasePath("/api/legacy/products")
                .setContentType(ContentType.XML)
                .build();
    }
}
