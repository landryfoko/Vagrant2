package com.libertas.camel.component.paypal;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

public class PayPalComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	PayPalConfiguration configuration = new PayPalConfiguration();
        setProperties(configuration, parameters);

        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("PayPal name must be specified.");
        }
        return new PayPalEndpoint(uri, this, configuration);

    }
}
