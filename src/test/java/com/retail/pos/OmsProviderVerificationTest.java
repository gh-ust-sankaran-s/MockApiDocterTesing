package com.retail.pos;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("oms-provider")
@PactBroker(url = "http://localhost:9292")
public class OmsProviderVerificationTest {

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(
                new HttpTestTarget(
                        "localhost",
                        8080,
                        "/"
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