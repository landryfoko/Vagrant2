package com.route.paypal.test;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class SimplePaymentPayPalTestWithCC {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/paypal/simple-payment-camel-paypal-context.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getOut().setHeader("CamelPayPalPayerFirstname", "FN");
		exchange.getOut().setHeader("CamelPayPalPayerLastname", "LN");
	    exchange.getOut().setHeader("CamelPayPalDescription", "This is the payment transaction description.");
		exchange.getOut().setHeader("CamelPayPalOperation", "SIMPLE_PAYMENT_CREDITCARD");
		exchange.getOut().setHeader("CamelPayPalCCNumber", "5500005555555559");
	    exchange.getOut().setHeader("CamelPayPalCCCVV2", "111");
	    exchange.getOut().setHeader("CamelPayPalCCExpMonth", "12");
	    exchange.getOut().setHeader("CamelPayPalCCExpYear", "2018");
	    exchange.getOut().setHeader("CamelPayPalCCType", "mastercard");
	    exchange.getOut().setHeader("CamelPayPalAmount", "14.17");
	    producer.send("direct:startNewSimplePayment", exchange);
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();
	}

}
