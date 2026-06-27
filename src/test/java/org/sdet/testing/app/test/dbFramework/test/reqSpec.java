package org.sdet.testing.app.test.dbFramework.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Utility class for building common request specifications used across test suite
 */
public class reqSpec {
    private static final String BASE_URL = "http://localhost:4000";

    /**
     * Base request specification for API calls
     */
    public static RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Request specification for order API calls
     */
    public static RequestSpecification orderSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Request specification for product API calls
     */
    public static RequestSpecification productSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/api/products")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Request specification for partner API calls with API key
     */
    public static RequestSpecification partnerSpec(String apiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/api/partner/orders")
                .addHeader("X-API-Key", apiKey)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}