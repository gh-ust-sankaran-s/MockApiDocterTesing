package org.sdet.testing.app;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

public class specFactory_Assignment_2 {
    public static String Base_URL="http://localhost:4000";
    private static final Properties APPLICATION_PROPERTIES = loadApplicationProperties();

    public static String config(String name, String fallback) {
        String systemValue = System.getProperty(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String envValue = System.getenv(name);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String propertyValue = APPLICATION_PROPERTIES.getProperty(name);
        return propertyValue == null || propertyValue.isBlank() ? fallback : propertyValue;
    }

    private static Properties loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = specFactory_Assignment_2.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load application.properties", exception);
        }
        return properties;
    }
    private static final String TOKEN_URL_CLIENT_ID = config("OAUTH_CLIENT_ID", "retail-ops-client");
    private static final String TOKEN_URL_CLIENT_SECRET = config("OAUTH_CLIENT_SECRET", "2a2729b27b47fe27b6412403d886ef4781bbff36b0e2b58e");
    private static final String VIEWER_CLIENT_ID = config("OAUTH_VIEWER_CLIENT_ID", "retail-viewer-client");
    private static final String VIEWER_CLIENT_SECRET = config("OAUTH_VIEWER_CLIENT_SECRET", "viewer-secret");
    private static final String EXPIRED_CLIENT_ID = config("OAUTH_EXPIRED_CLIENT_ID", "retail-expired-client");
    private static final String EXPIRED_CLIENT_SECRET = config("OAUTH_EXPIRED_CLIENT_SECRET", "expired-secret");
    private static final String API_KEY = config("RETAIL_API_KEY", "retail-demo-key");
    public static String apiReturn(){
        return API_KEY;
    }

    public static String AuthOrder() {
        return given()
                .baseUri(Base_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .auth().preemptive().basic("OAUTH_CLIENT_ID", "OAUTH_CLIENT_SECRET")
                .formParam("grant_type", "client_credentials")

                .when()
                .post("/api/oauth/token")
                .then()
                .statusCode(200)
                .body("token_type", equalToIgnoringCase("Bearer"))
                .body("expires_in", greaterThan(0))
                .body("access_token", not(emptyOrNullString()))
                .extract()
                .path("access_token");
    }
    public static ResponseSpecification unauthorized401() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .build();
    }
    public static ResponseSpecification okSuccess() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }
    public static ResponseSpecification forbidden() {
        return new ResponseSpecBuilder()
                .expectStatusCode(403)
                .build();
    }
    public static RequestSpecification unauthSpec() {
        return new RequestSpecBuilder()

                .setBaseUri(Base_URL)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
    public static RequestSpecification fetchwithAPIkey(String key) {
        return new RequestSpecBuilder()
                .setBaseUri(Base_URL)
                .setBasePath("/api/partner/orders")
                .addHeader("X-API-Key", key)
                .build();

    }
    public static RequestSpecification noAPIkey() {
        return new RequestSpecBuilder()
                .setBaseUri(Base_URL)
                .setBasePath("/api/partner/orders")
                .build();
    }

}
