package com.libertas.camel.component.braintree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONObject;

import lombok.extern.slf4j.Slf4j;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.braintreegateway.*;
import com.thoughtworks.xstream.*;

/**
 * The PayPal producer.
 */
@Slf4j
public class BraintreeProducer extends DefaultProducer {
    public BraintreeProducer(BraintreeEndpoint endpoint) {
        super(endpoint);
    }
    @Override
    public BraintreeEndpoint getEndpoint() {
        return (BraintreeEndpoint) super.getEndpoint();
    }

    public void process(Exchange exchange) throws Exception {
        log.info(""+exchange.getIn().getBody());
        String operation=exchange.getIn().getHeader(Constants.CamelBraintreeOperation,String.class);
        if(StringUtils.isEmpty(operation)){
        	throw new IllegalArgumentException("Missing operation in header");
        }
        Operation op=Operation.valueOf(operation.toUpperCase());
        String billingStartDate = exchange.getIn().getHeader(Constants.CamelBraintreeBillingStartDate,String.class);
		String creditCardNumber = exchange.getIn().getHeader(Constants.CamelBraintreeCCNumber,String.class);
		String savedCreditCardId = exchange.getIn().getHeader(Constants.CamelBraintreeSavedCC,String.class);
		String cVV2 = exchange.getIn().getHeader(Constants.CamelBraintreeCCCVV2,String.class);
		String expMonth = exchange.getIn().getHeader(Constants.CamelBraintreeCCExpMonth,String.class);
		String expYear = exchange.getIn().getHeader(Constants.CamelBraintreeCCExpYear,String.class);
		String creditCardType = exchange.getIn().getHeader(Constants.CamelBraintreeCCType,String.class);
		String payerFirstname = exchange.getIn().getHeader(Constants.CamelBraintreePayerFirstname,String.class);
		String payerLastname = exchange.getIn().getHeader(Constants.CamelBraintreePayerLastname,String.class);
		String description = exchange.getIn().getHeader(Constants.CamelBraintreeDescription,String.class);
		String amount = exchange.getIn().getHeader(Constants.CamelBraintreeAmount,String.class);
		String item = exchange.getIn().getHeader(Constants.CamelBraintreeItem,String.class);
		Boolean makeDefault = exchange.getIn().getHeader(Constants.CamelBraintreeMakeDefault,Boolean.class);
		Boolean failOnDuplicatePaymentMethod = exchange.getIn().getHeader(Constants.CamelBraintreeFailOnDuplicatePaymentMethod,Boolean.class);
		Boolean verifyCard = exchange.getIn().getHeader(Constants.CamelBraintreeVerifyCard,Boolean.class);
		String customerId = exchange.getIn().getHeader(Constants.CamelBraintreeCustomerID,String.class);
		String tokenId = exchange.getIn().getHeader(Constants.CamelBraintreeTokenID,String.class);
		String transactionId = exchange.getIn().getHeader(Constants.CamelBraintreeTransactionID,String.class);


        switch (op) {
        case RECURRING_PAYMENT_CREDITCARD:
        	CreateRecurringPaymentsCreditCard(exchange,savedCreditCardId,billingStartDate,amount,item);
			break;
        case SAVE_CREDITCARD_PAYMENT:
        	saveCreditCardPayments(exchange,
        			creditCardNumber,creditCardType,expMonth,expYear,payerFirstname,payerLastname,cVV2, makeDefault,customerId,failOnDuplicatePaymentMethod,verifyCard,tokenId);
			break;
        case SIMPLE_PAYMENT_WITH_SAVED_CREDITCARD:
        	createSimplePaymentsWithSavedCreditCard(exchange,
        			savedCreditCardId,amount,description,item,transactionId);
			break;
        case DELETE_CREDITCARD:
        	deleteCreditCard(exchange,	savedCreditCardId);
			break;
		default:
			throw new UnsupportedOperationException(operation);
		}
    }

    private void CreateRecurringPaymentsCreditCard(Exchange exchange,String savedCreditCardId,String billingStartDate,String amount,String item)  throws Exception{
    	TransactionRequest request = new TransactionRequest()
        .amount(new BigDecimal("100.00"))
        .merchantAccountId("your_merchant_account")
        .creditCard()
            .number("5105105105105100")
            .expirationDate("05/2020")
            .done();

    Result<Transaction> result = getGateway().transaction().credit(request);
    }



    private BraintreeGateway getGateway() {
		return getEndpoint().getBraintreeConfiguration().getGateway();
	}

	private void saveCreditCardPayments(Exchange exchange,
    		String creditCardNumber,String creditCardType,String expMonth,String expYear,
    		String payerFirstname,String payerLastname, String cvv, Boolean makeDefault, String customerId, Boolean failOnDuplicatePaymentMethod, Boolean verifyCard, String tokenId ) throws Exception{


			CustomerRequest customerRequest = new CustomerRequest()
		 		.id(customerId).firstName(payerFirstname).lastName(payerLastname);
			getGateway().customer().create(customerRequest);

			CreditCardRequest creditCard = new CreditCardRequest()
		    .customerId(customerId)
		    .number(creditCardNumber)
		    .expirationYear(expYear)
		    .expirationMonth(expMonth)
		    .cvv(cvv)
		    .options()
		    .makeDefault(makeDefault==null?false:makeDefault)
		    .failOnDuplicatePaymentMethod(failOnDuplicatePaymentMethod==null?true:failOnDuplicatePaymentMethod)
		    .verifyCard(verifyCard==null?true:verifyCard)
		    .done();
			if(tokenId!=null){
				creditCard.token(tokenId);
			}
		Result<CreditCard> result = getGateway().creditCard().create(creditCard);
			if(!result.isSuccess()){
	        	log.info("Braintree response message:{}",result.getMessage());
	        	throw new IllegalStateException(result.getMessage());
	        }else{
	        	CreditCard  cc=result.getTarget();
	        	XStream xstream = new XStream(new JettisonMappedXmlDriver());
    			xstream.setMode(XStream.NO_REFERENCES);
				xstream.alias("CreditCard", CreditCard.class);
				Map<String,Object>jsonCC=(Map<String, Object>)((Map<String, Object>) org.json.simple.JSONValue.parse(xstream.toXML(cc))).get("CreditCard");
				jsonCC.put("braintreeId",cc.getCustomerId());
				jsonCC.put("id",cc.getUniqueNumberIdentifier());
				exchange.getIn().setHeader(Constants.CamelBraintreeSavedCC,jsonCC);
				log.info("Braintree credit card added:{}",jsonCC);
	        }
    }

	private String trim(String e){
		if(e==null){
			return null;
		}else if(e.length()<17){
			return e;
		}else{
			return e.substring(e.length()-17);
		}
	}
    private void createSimplePaymentsWithSavedCreditCard(Exchange exchange,
    		String savedCreditCardId,String payedAmount,String description, String item, String transactionId) throws Exception{
    	JSONObject o= new JSONObject();
    	o.put("description", description);
    	o.put("item", item);
    	o.put("transactionId", transactionId);
    	TransactionRequest request = new TransactionRequest()
        .paymentMethodToken(savedCreditCardId)
        .amount(new BigDecimal(payedAmount))
        .purchaseOrderNumber(trim(item)).orderId(trim(transactionId))
        .deviceData(o.toString(2))
        .options().submitForSettlement(true).done()
        .creditCard()
        .done();
    	Result<Transaction> result = getGateway().transaction().sale(request);
    	if(!result.isSuccess()){
        	log.info("Braintree response message:{}",result.getMessage());
        	throw new IllegalStateException(result.getMessage());
        }else{
        	Transaction  cc=result.getTarget();
    		log.info("Braintree purchase completed. OrderId:{}",result.getTarget().getOrderId());
    		log.info("Braintree purchase completed. Raw result:{}",result.getTarget().getProcessorResponseText());
    		log.info("Braintree purchase completed. Raw result:{}",result.getMessage());
    		log.info("Braintree purchase completed. Raw result:{}",result.getErrors());
    		log.info("Braintree purchase completed. Raw result:{}",result.getParameters());
        	XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.setMode(XStream.NO_REFERENCES);
			xstream.alias("Transaction", Transaction.class);
			Map<String,Object>jsonCC=(Map<String, Object>)((Map<String, Object>) org.json.simple.JSONValue.parse(xstream.toXML(cc)));

			exchange.getIn().setHeader(Constants.CamelBraintreeSavedCC,jsonCC.get("Transaction"));
			log.info("Braintree purchase completed:{}",jsonCC.get("Transaction"));
        }

    }

    private void deleteCreditCard(Exchange exchange,String creditCardId) throws Exception{
    	log.info("Deleting credit card:{}",creditCardId);
    	Result<CreditCard> result=getGateway().creditCard().delete(creditCardId);
       	if(!result.isSuccess()){
        	log.info("Braintree response message:{}",result.getMessage());
        	throw new IllegalStateException(result.getMessage());
        }else{
        	log.info("Braintree credit card {} deleted from vault",creditCardId);
        }
    }
}