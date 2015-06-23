package com.route.braintree.test;


import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class RecurringPaymentPayPalTestWithCC {

	public static void main(String[] args) throws Exception {
		File f= new File(".");
		System.out.println(f.getAbsolutePath());
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"recurring-payment-camel-paypal-context.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getOut().setHeader("CamelBraintreeOperation", "RECURRING_PAYMENT_CREDITCARD");
		exchange.getOut().setHeader("CamelBraintreeBillingStartDate", "2015-05-25T00:00:00:000Z");
	    exchange.getOut().setHeader("CamelPayPalCCNumber", "4745425765192217");
	    exchange.getOut().setHeader("CamelPaypalCCCVV2", "962");
	    exchange.getOut().setHeader("CamelPayPalCCExpMonth", "05");
	    exchange.getOut().setHeader("CamelPayPalCCExpYear", "2014");
	    exchange.getOut().setHeader("CamelPayPalCCType", "Visa");
	    exchange.getOut().setHeader("CamelPayPalAmount", "26.65");
	    exchange.getOut().setHeader("CamelPayPalItem", "Product Item Name");
	    producer.send("direct:startNewRecurrentPayment", exchange);
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();
	}

}
