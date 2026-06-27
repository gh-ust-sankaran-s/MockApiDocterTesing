package org.sdet.testing.app;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.specification.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogBaselineTest {
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            System.getenv().getOrDefault("BASE_URL", "http://localhost:4000")
    );

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.basePath = "api/";
    }


    static ResponseSpecification okJson = new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .expectStatusCode(200)
            .build();
    static RequestSpecification pathp = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .addPathParam("id", 102)
            .build();
    static RequestSpecification req = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setBasePath("/products")
            .setAccept(ContentType.JSON)
            .build();
    static ResponseSpecification resp = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectResponseTime(lessThan(1500L))
            .build();
    static RequestSpecification req1 = new RequestSpecBuilder()
            .setBaseUri("http://localhost:4000")
            .setBasePath("/api/products")
            .setAccept(ContentType.JSON)
            .build();


    @Test
    @DisplayName("M1:Get /products returns 200 JSON with a populated collection")
    void getProducts_isHealthyJson() {
        given()
                .log().ifValidationFails()
                .spec(req)
                .when()
                .get("")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("items.size()", greaterThan(0));
    }

    @Test
    @DisplayName("M1:Get /products return with Item as Array")
    void getProducts_iscontainsItem() {
        given()
                .log().ifValidationFails()
                .when()
                .get("/products")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(900L))
                .body("items.size()", greaterThan(0))
                .body("items[0].id", notNullValue())
                .body("items[0].name", instanceOf(String.class))
                .body("items[0].price", greaterThanOrEqualTo(0));

    }

    @Test
    @DisplayName("M3: Get the Products using the path param")
    void get_productsByPathParams() {
        given()
                .log().ifValidationFails()
                .pathParam("id", 101)
                .when()
                .get("/products/{id}")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(101));
    }

    @Test
    @DisplayName("M3: Get the Products using the Query param")
    void get_productsByQueryParams() {
        given()
                .log().ifValidationFails()
                .queryParam("category", "Footwear")
                .when()
                .get("/products/")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("items.category", everyItem(is("Footwear")));
    }

    @Test
    @DisplayName("Baseline Get / products")
    void get_products_fromtheCatlog() {
        given()
                .log().ifValidationFails()
                .when()
                .get("/products")
                .then()
                .log().ifValidationFails()
                .spec(resp)
                .body("items.size()", not(empty()));
    }

    @Test
    @DisplayName("Get / products/{id} with a path param")
    void get_products_fromtheid() {
        given()
                .spec(pathp)
                .when()
                .get("/api/products/{id}")
                .then()
                .spec(okJson)
                .body("id", equalTo(102))
                .body("name", not(emptyString()))
                .body("price", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("Baseline Get / products")
    void Shared_req_and_resp() {
        given()
                .log().ifValidationFails()
                .when()
                .get("/products")
                .then()
                .log().ifValidationFails()
                .spec(resp)
                .body("items.size()", not(empty()));
    }

    @Test
    @DisplayName("Deserialize product into typed object")
    void deserializeProduct() {
        Product p =
                given()
                        .spec(req1)
                        .when()
                        .get("/{id}", 106)
                        .then()
                        .spec(okJson)
                        .extract()
                        .as(Product.class);
        assertEquals(106, p.id());
        assertTrue(p.price() >= 0);
    }

    static ResponseSpecification postJson = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectContentType(ContentType.JSON)
            .build();


    static RequestSpecification orderJson = new RequestSpecBuilder()
            .setBaseUri("http://localhost:4000")
            .setBasePath("/api/orders")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + "demo-token-1-token")
            .build();
    static RequestSpecification cartJson = new RequestSpecBuilder()
            .setBaseUri("http://localhost:4000")
            .setBasePath("/api/cart/items")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + "demo-token-1-token")
            .build();
    static RequestSpecification orders = new RequestSpecBuilder()
            .setBaseUri("http://localhost:4000")
            .setBasePath("/api/orders")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + "Bearer demo-token-1-customer")
            .build();

    //authentication
    private static RequestSpecification authentiaction(String basePath, String token) {
        return new RequestSpecBuilder()
                .setBasePath(basePath)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .addHeader("Authentication", "Bearer " + token)
                .build();
    }

    @Test
    @DisplayName("Create an order with POST")
    void createOrder_persists() {
        var order = Map.of("productId", 101, "quantity", 2, "size", "Standard", "address", "Rajaplayam");
        given()
                .spec(cartJson)
                .body(order)
                .when()
                .post("")
                .then()
                .spec(postJson);

        String id = given().spec(orderJson).body(order).when().post("")
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .extract().path("id").toString();
        given()
                .spec(orders)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .spec(okJson)
                .body("id.toString()", equalTo(id));

    }
    private static String config(String name , String fallback)
    {
        String systemValue=System.getProperty(name);
        if(systemValue!=null && !systemValue.isBlank())
        {
            return systemValue;
        }
        String envValue=System.getenv(name);
        return envValue==null||envValue.isBlank()?fallback:envValue;
    }
    private static final String TOKEN_URL_CLIENT_ID = config("OAUTH_CLIENT_ID", "retail-ops-client");
    private static final String TOKEN_URL_CLIENT_SECRET = config("OAUTH_CLIENT_SECRET", "ops-secret");
    private static final String VIEWER_CLIENT_ID = config("OAUTH_VIEWER_CLIENT_ID", "retail-viewer-client");
    private static final String VIEWER_CLIENT_SECRET = config("OAUTH_VIEWER_CLIENT_SECRET", "viewer-secret");
    private static final String EXPIRED_CLIENT_ID = config("OAUTH_EXPIRED_CLIENT_ID", "retail-expired-client");
    private static final String EXPIRED_CLIENT_SECRET = config("OAUTH_EXPIRED_CLIENT_SECRET", "expired-secret");
    private static final String API_KEY = config("RETAIL_API_KEY", "retail-demo-key");

    @Test
    @DisplayName("Authentivate then create an order")
    void AuthOrder()
    {
        String token =
                given()
                        .baseUri(BASE_URL)
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .auth().preemptive().basic("retail-ops-client","ops-secret")
                        .formParam("grant_type", "client_credentials")

                        .when()
                        .post("/oauth/token")
                        .then()
                        .statusCode(200)
                        .body("token_type",equalToIgnoringCase("Bearer"))
                        .body("expires_in",greaterThan(0))
                        .body("access_token",not(emptyOrNullString()))
                        .extract()
                        .path("access_token");
        given()
                .baseUri(baseURI)
                .contentType("application/json")
                .accept("application/json")
                .auth()
                .oauth2(token)
                .body(
                        Map.of("items",List.of(101,107), "currency","INR" ) )
                .when()
                .post("/secure/orders")
                .then()
                .statusCode(201)
                .body(  "orderId",  notNullValue())
                .body(   "status",equalTo("CREATED"))
                .body("items.size()",equalTo(2))
                .body(matchesJsonSchemaInClasspath("schemas/json/order.schema.json" ));
    }
    //Build & reuse an authed spec
    @Test
    @DisplayName("Reuse & Build an authed spec")
    void reuseAuthOrder() {
        given()
                .baseUri(baseURI)
                .contentType("application/json")
                .accept("application/json")
                .auth()
                .oauth2(specFactory.AuthOrder())
                .body(
                        Map.of("items",List.of(101,107), "currency","INR" ) )
                .when()
                .post("/secure/orders")
                .then()
                .statusCode(201)
                .body(  "orderId",  notNullValue())
                .body(   "status",equalTo("CREATED"))
                .body("items.size()",equalTo(2))
                .body(matchesJsonSchemaInClasspath("schemas/json/order.schema.json" ));
    }
}
