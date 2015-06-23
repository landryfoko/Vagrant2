package com.route.paypal.test;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class DeleteSavedCreditCard {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/paypal/detete-credit-card-camel-paypal-context.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getOut().setHeader("CamelPayPalOperation", "DELETE_CREDITCARD");
		exchange.getOut().setHeader("CamelPaypalSavedCC", "CARD-6U982155U0612442DKNA34JA");
	    producer.send("direct:deleteSavedCreditCard", exchange);
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();

	}

}
