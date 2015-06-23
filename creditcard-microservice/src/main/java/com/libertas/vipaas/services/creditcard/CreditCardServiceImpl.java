package com.libertas.vipaas.services.creditcard;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kafka.javaapi.producer.Producer;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.CreditCardProcessingFailure;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchCreditCardException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

@Service
@ConfigurationProperties("creditcard")
@Slf4j
public class CreditCardServiceImpl implements CreditCardService {
	//@Autowired
	private Producer<String,Object> producer;
	@Autowired
	private MetadataProvider metadataProvider;

	@Autowired
	private BraintreeGateway gateway;
	@Autowired
	RestTemplateProxy restTemplateProxy;

	private String databaseServiceName;


	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public JSONObject addCreditCard(JSONObject metadata) throws NoSuchProductException,MissingFieldException, CreditCardProcessingFailure {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		String CVV=(String)metadata.get("CVV");
		String expMonth=(String)metadata.get("expMonth");
		String expYear=(String)metadata.get("expYear");
		String type=(String)metadata.get("type");
		String firstName=(String)metadata.get("firstName");
		String lastName=(String)metadata.get("lastName");
		String creditCardNumber=(String)metadata.get("creditCardNumber");
		Boolean makeDefault=(Boolean)metadata.get("makeDefault");
		String friendlyName=(String)metadata.get("friendlyName");
		Boolean verifyCard=(Boolean)metadata.get("verifyCard");
		Boolean failOnDuplicatePaymentMethod=(Boolean)metadata.get("failOnDuplicatePaymentMethod");
		String tokenId=(String)metadata.get("tokenId");


		if(StringUtils.isEmpty(CVV)){
			throw new MissingFieldException("CVV");
		}
		if(StringUtils.isEmpty(expMonth)){
			throw new MissingFieldException("expMonth");
		}
		if(StringUtils.isEmpty(expYear)){
			throw new MissingFieldException("expMonth");
		}
		if(StringUtils.isEmpty(type)){
			throw new MissingFieldException("type");
		}
		if(StringUtils.isEmpty(firstName)){
			throw new MissingFieldException("firstName");
		}
		if(StringUtils.isEmpty(lastName)){
			throw new MissingFieldException("lastName");
		}
		if(StringUtils.isEmpty(creditCardNumber)){
			throw new MissingFieldException("creditCardNumber");
		}
		if(StringUtils.isEmpty(friendlyName)){
			throw new MissingFieldException("friendlyName");
		}

		Map<String,Object>saved=saveCreditCardPayments(creditCardNumber, type, expMonth, expYear, firstName, lastName, CVV,
				makeDefault, customerId, failOnDuplicatePaymentMethod, verifyCard, tokenId);
		JSONObject creditcard= new JSONObject(saved);
		creditcard.put("customerId", customerId);
		creditcard.put("tenantId", tenantId);
		creditcard.put("friendlyName", friendlyName);
		creditcard.put("id", UUID.randomUUID().toString());

		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/creditcard", JSONHelper.marshall(creditcard));
		return metadataProvider.filter(creditcard);
	}


	private Map<String, Object> saveCreditCardPayments(
    		String creditCardNumber,String creditCardType,String expMonth,String expYear,
    		String payerFirstname,String payerLastname, String cvv, Boolean makeDefault, String customerId, Boolean failOnDuplicatePaymentMethod, Boolean verifyCard, String tokenId ) throws CreditCardProcessingFailure{


			CustomerRequest customerRequest = new CustomerRequest()
		 		.id(customerId).firstName(payerFirstname).lastName(payerLastname);
			gateway.customer().create(customerRequest);

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
		Result<CreditCard> result = gateway.creditCard().create(creditCard);
			if(!result.isSuccess()){
	        	throw new CreditCardProcessingFailure(result.getMessage());
	        }else{
	        	CreditCard  cc=result.getTarget();
	        	XStream xstream = new XStream(new JettisonMappedXmlDriver());
    			xstream.setMode(XStream.NO_REFERENCES);
				xstream.alias("CreditCard", CreditCard.class);
				Map<String,Object>jsonCC=(Map<String, Object>)((Map<String, Object>) org.json.simple.JSONValue.parse(xstream.toXML(cc))).get("CreditCard");
				jsonCC.put("braintreeCustomerId",cc.getCustomerId());
				jsonCC.put("braintreeId",cc.getUniqueNumberIdentifier());
				return jsonCC;
	        }
    }

	@Override
	public JSONObject getCreditCardById(String creditcardId) throws NoSuchCreditCardException {
		JSONObject unmarshalled=getCreditCardByIdInternal(creditcardId);
		metadataProvider.filter(unmarshalled);
		return unmarshalled;
	}

	private JSONObject getCreditCardByIdInternal(String creditcardId) throws NoSuchCreditCardException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> creditcards=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/creditcard/"+creditcardId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(creditcards.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			return unmarshalled;
		}
		throw new NoSuchCreditCardException("No Such CreditCard");
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);

		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");


		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> creditCards=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/creditcard/search/findByCustomerIdAndTenantId?customerId="+customerId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(creditCards.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;

	}

	@Override
	public void updateCreditCard(String creditcardId, JSONObject metadata) throws NoSuchCreditCardException {
		JSONObject cc=getCreditCardByIdInternal(creditcardId);
		if(StringUtils.isNotEmpty((String)metadata.get("friendlyName"))){
			cc.put("friendlyName", metadata.get("friendlyName")); //Only allowed to update friendly name
			restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/creditcard/"+cc.get("id"), JSONHelper.marshall(cc));
		}
	}

	@Override
	public void deleteCreditCardById( String creditcardId) throws NoSuchCreditCardException, CreditCardProcessingFailure {
		JSONObject creditCard=getCreditCardByIdInternal(creditcardId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		boolean exists=existsCreditCard(creditcardId);
		if(!exists){
			throw new NoSuchCreditCardException("No such credit card");
		}
		creditCard.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		creditCard.put("customerId", customerId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		creditCard.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/creditcard/"+creditCard.get("id"), JSONHelper.marshall(creditCard));
	}

	private boolean existsCreditCard(String creditcardId){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		JSONObject cc=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/creditcard/"+creditcardId, JSONObject.class);
		if(cc==null){
			return false;
		}
		cc=JSONHelper.unmarshall(cc);
		return cc!=null && cc.get("tenantId").equals(tenantId) && cc.get("customerId").equals(customerId) ;
	}

}
