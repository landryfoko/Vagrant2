package com.route.paypal.test;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class SimplePaymentPayPalTestWithSavedCC {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/paypal/simple-payment-camel-paypal-contex-with-saved-CC.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getOut().setHeader("CamelPayPalOperation", "SIMPLE_PAYMENT_WITH_SAVED_CREDITCARD");
		exchange.getOut().setHeader("CamelPaypalSavedCC", "CARD-5V820365KB1374948KNAYEWA");
		exchange.getOut().setHeader("CamelPayPalDescription", "This is the payment transaction description.");
		exchange.getOut().setHeader("CamelPayPalAmount", "32.49");
	    producer.send("direct:startNewSimplePaymentWithSavedCC", exchange);
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();
	}

}
