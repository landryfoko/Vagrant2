package com.route.paypal.test;

import java.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class CreditCardPaymentsTestWithCC {

	public static void main(String[] args) throws Exception {
		String customerId=UUID.randomUUID().toString();
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/braintree/save-creditcard-camel-braintree-context.xml");
		CamelContext camelContext = (CamelContext) context.getBean("camelContext");
		ProducerTemplate producer;
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		Exchange exchange = new DefaultExchange(camelContext);
	    exchange.getOut().setHeader("Description", "This is the payment transaction description");
		exchange.getOut().setHeader("Operation", "SAVE_CREDITCARD_PAYMENT");
	    exchange.getOut().setHeader("CVV2", "962");
	    exchange.getOut().setHeader("CreditCardNumber", "4111 1111 1111 1111");
	    exchange.getOut().setHeader("ExpMonth", "11");
	    exchange.getOut().setHeader("ExpYear", "2018");
	    exchange.getOut().setHeader("Firstname", "Alexandre3");
		exchange.getOut().setHeader("Lastname", "Paul3");
		exchange.getOut().setHeader("FailOnDuplicatePaymentMethod", "false");
		exchange.getOut().setHeader("CustomerID", customerId);
	    producer.send("direct:startsaveCCPayment", exchange);
	    Map<String,Object> result=(Map<String,Object>)exchange.getIn().getHeader("SavedCreditCard");
	   String id=result.get("token")+"";
	    
	    System.out.println("result:"+id);
		Thread.sleep(5000);
		System.out.println("Starting purchase:"+id);
		 context = new ClassPathXmlApplicationContext(
					"/braintree/simple-payment-camel-braintree-contex.xml");
			camelContext = (CamelContext) context.getBean("camelContext");
			camelContext.start();
			producer = camelContext.createProducerTemplate();
			exchange = new DefaultExchange(camelContext);
			exchange.getOut().setHeader("Operation", "SIMPLE_PAYMENT_WITH_SAVED_CREDITCARD");
			exchange.getOut().setHeader("SavedCreditCard", id);
			exchange.getOut().setHeader("Description", "This is the payment transaction description.");
			exchange.getOut().setHeader("Amount", "32.49");
			producer.send("direct:startNewSimplePaymentWithSavedCC", exchange);
	    //System.out.println("Credit card ID found:"+id);
		    
			System.out.println("Purchase ended"+id);
	  /*  context = new ClassPathXmlApplicationContext(
				"/braintree/detete-creditcard-camel-braintree-context.xml");
		camelContext = (CamelContext) context.getBean("camelContext");
		camelContext.start();
		producer = camelContext.createProducerTemplate();
		exchange = new DefaultExchange(camelContext);
		exchange.getOut().setHeader("Operation", "DELETE_CREDITCARD");
		exchange.getOut().setHeader("SavedCreditCard", id);
	    producer.send("direct:deleteSavedCreditCard", exchange);*/
		System.in.read();
		// Thread.sleep(100000);
		camelContext.stop();
	}

}
