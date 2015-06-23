package com.route.paypal.test;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class SaveCreditCardPaymentsTestWithCC {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/paypal/save-CC-payments-camel-paypal-context.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
	    exchange.getOut().setHeader("CamelPayPalDescription", "This is the payment transaction description");
		exchange.getOut().setHeader("CamelPayPalOperation", "SAVE_CREDITCARD_PAYMENT");
	    exchange.getOut().setHeader("CamelPayPalCCType", "visa");
	    exchange.getOut().setHeader("CamelPayPalCCNumber", "4446283280247004");
	    exchange.getOut().setHeader("CamelPayPalCCExpMonth", "11");
	    exchange.getOut().setHeader("CamelPayPalCCExpYear", "2018");
	    exchange.getOut().setHeader("CamelPayPalPayerFirstname", "Alexandre");
		exchange.getOut().setHeader("CamelPayPalPayerLastname", "Paul");
	    producer.send("direct:startsaveCCPayment", exchange);
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();
	}

}
