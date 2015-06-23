package com.libertas.camel.component.paypal;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a PayPal endpoint.
 */
public class PayPalEndpoint extends DefaultEndpoint {
	PayPalConfiguration payPalConfiguration;


    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("No consumer for PayPal");
    }

    public PayPalEndpoint(String uri, PayPalComponent payPalComponent,	PayPalConfiguration payPalConfiguration) {
    	 super(uri, payPalComponent);
         this.payPalConfiguration = payPalConfiguration;
	}

	public Producer createProducer() throws Exception {
        return new PayPalProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }

	public PayPalConfiguration getPayPalConfiguration() {
		return payPalConfiguration;
	}

	public void setPayPalConfiguration(PayPalConfiguration payPalConfiguration) {
		this.payPalConfiguration = payPalConfiguration;
	}
}
