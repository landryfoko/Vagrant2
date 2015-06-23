package com.libertas.camel.component.braintree;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a PayPal endpoint.
 */
public class BraintreeEndpoint extends DefaultEndpoint {
	BraintreeConfiguration payPalConfiguration;


    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("No consumer for PayPal");
    }

    public BraintreeEndpoint(String uri, BraintreeComponent payPalComponent,	BraintreeConfiguration payPalConfiguration) {
    	 super(uri, payPalComponent);
         this.payPalConfiguration = payPalConfiguration;
	}

	public Producer createProducer() throws Exception {
        return new BraintreeProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }

	public BraintreeConfiguration getBraintreeConfiguration() {
		return payPalConfiguration;
	}

	public void setPayPalConfiguration(BraintreeConfiguration payPalConfiguration) {
		this.payPalConfiguration = payPalConfiguration;
	}
}
