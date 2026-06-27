package com.retail.pos;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

@Provider("oms-provider")
@PactBroker(url = "http://127.0.0.1:9292")
public class OmsProviderVerificationTest {

    @RegisterExtension
    static final WireMockExtension wireMock =
            WireMockExtension.newInstance()
                    .options(wireMockConfig().port(4010))
                    .build();

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(
                new HttpTestTarget(
                        "127.0.0.1",
                        4010
                )
        );
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("order 123 exists")
    void orderExists() {
        System.out.println("Order 123 exist");
    }


    @State("inventory available for SKU-9")
    void inventoryAvailable() {
        System.out.println("inventory available for SKU-9");
    }

    @State("order 123 exists and Update order")
    void orderUpdate() {
        System.out.println("order 123 exists and Update order");
    }
    @State("SKU-9 has stock")
    void skuHasStock() {
        System.out.println("SKU-9 has stock");
    }
    @State("order 123 exists and can be cancelled")
    void  orderCancel()
    {
        System.out.println("order 123 exists and can be cancelled");
    }
}