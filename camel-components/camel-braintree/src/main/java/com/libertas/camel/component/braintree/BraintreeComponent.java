package com.libertas.camel.component.braintree;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;


public class BraintreeComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	BraintreeConfiguration configuration = new BraintreeConfiguration();
        setProperties(configuration, parameters);

        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("PayPal name must be specified.");
        }
        return new BraintreeEndpoint(uri, this, configuration);

    }
}
