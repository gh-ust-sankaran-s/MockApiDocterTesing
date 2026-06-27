package org.sdet.testing.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

public class specFactory {
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            System.getenv().getOrDefault("BASE_URL", "http://localhost:4000")
    );

    private static String config(String name, String fallback) {
        String systemValue = System.getProperty(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String envValue = System.getenv(name);
        return envValue == null || envValue.isBlank() ? fallback : envValue;
    }

    private static final String TOKEN_URL_CLIENT_ID = config("OAUTH_CLIENT_ID", "retail-ops-client");
    private static final String TOKEN_URL_CLIENT_SECRET = config("OAUTH_CLIENT_SECRET", "2a2729b27b47fe27b6412403d886ef4781bbff36b0e2b58e");
    private static final String VIEWER_CLIENT_ID = config("OAUTH_VIEWER_CLIENT_ID", "retail-viewer-client");
    private static final String VIEWER_CLIENT_SECRET = config("OAUTH_VIEWER_CLIENT_SECRET", "viewer-secret");
    private static final String EXPIRED_CLIENT_ID = config("OAUTH_EXPIRED_CLIENT_ID", "retail-expired-client");
    private static final String EXPIRED_CLIENT_SECRET = config("OAUTH_EXPIRED_CLIENT_SECRET", "expired-secret");
    private static final String API_KEY = config("RETAIL_API_KEY", "retail-demo-key");

    public static String AuthOrder() {
        return given()
                        .baseUri(BASE_URL)
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .auth().preemptive().basic("retail-ops-client", TOKEN_URL_CLIENT_SECRET)
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

}