package org.sdet.testing.app.dbFramework.week2_assess.test.specfactory.respSpec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class responseSpec {
    public static ResponseSpecification unauth401() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }
    public static ResponseSpecification okSuccess() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }
    public static ResponseSpecification forbidn403() {
        return new ResponseSpecBuilder()
                .expectStatusCode(403)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }
    public static ResponseSpecification orderNotFn404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }

    public static ResponseSpecification validate404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }
    public static ResponseSpecification validate409() {
        return new ResponseSpecBuilder()
                .expectStatusCode(409)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }

    public static ResponseSpecification orderCreated201WithSchema() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }

    public static ResponseSpecification orderOk200WithSchema() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"))
                .build();
    }

}
