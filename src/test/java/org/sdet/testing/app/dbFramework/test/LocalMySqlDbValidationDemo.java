package org.sdet.testing.app.dbFramework.test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.sdet.testing.app.dbFramework.config.DatabaseConfig;
import org.sdet.testing.app.dbFramework.support.Dbsupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sdet.testing.app.dbFramework.model.OrderRow;
import org.sdet.testing.app.specFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class LocalMySqlDbValidationDemo {
    private static final List<Long> createdOrderIds = new ArrayList<Long>();
    private static Dbsupport database;
    private static String token;
    private static final String BASE_URL = "http://localhost:4000";

    @BeforeAll
    static void setup()
    {
        database = new Dbsupport(DatabaseConfig.fromEnvironment());
        token = specFactory.AuthOrder();
    }

    private static RequestSpecification setupAuth() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setAuth(RestAssured.oauth2(token))
                .build();
    }

    @Test
    @DisplayName("First Test")
    void localMySqlIsReachable() throws Exception{
        assertTrue(database.isReachable());
    }
    @Test
    @DisplayName("Create Order - Is Persisted")
    void createOrder_isPersisted() throws Exception{
        Response c=given().spec(setupAuth()).body(Map.of(
                        "items", List.of(101, 107)
                ))
                .when().post("")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();
        int id=c.path("orderId");

        OrderRow row =Dbsupport.findOrder(id);
        assertNotNull(row,"Order must be persisted");
        assertEquals("CREATED",row.getStatus());
        assertEquals(0,row.getTotal().compareTo(c.jsonPath().getObject("total", BigDecimal.class)));
    }
}
