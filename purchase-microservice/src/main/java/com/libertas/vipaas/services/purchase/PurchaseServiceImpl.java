package com.libertas.vipaas.services.purchase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("purchase")
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {


	@Autowired @Getter @Setter
	private RestTemplateProxy solrRestTemplateProxy;
	@Autowired @Getter @Setter
	private RestTemplateProxy entitlementRestTemplateProxy;
	@Autowired @Getter @Setter
	private RestTemplateProxy databaseRestTemplateProxy;
	@Autowired @Getter @Setter
	private MetadataProvider metadataProvider;
	@Getter @Setter
	private String purchaseTopic;
	@Autowired @Getter @Setter
	private BraintreeGateway gateway;

	//@Autowired
	@Getter @Setter
	private ProducerTemplate producer;
	
	@Getter @Setter
	private String entitlementCreationUrlTemplate;
	@Getter @Setter
	private String databaseServiceName;
	@Getter @Setter
	private String solrReadUrl;


	@SuppressWarnings("unchecked")
	public JSONObject purchase(JSONObject order) throws  MissingFieldException, NoSuchOfferException, NoSuchProductException  {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		String productId=(String)order.get("productId");
		String offerId=(String)order.get("offerId");
		String creditCardId=(String)order.get("creditCardId");
		if(StringUtils.isEmpty(productId)){
			throw new MissingFieldException("Missing productId field in request");
		}
		if(StringUtils.isEmpty(offerId)){
			throw new MissingFieldException("Missing offerId field in request");
		}
		if(StringUtils.isEmpty(creditCardId)){
			throw new MissingFieldException("Missing creditCardId field in request");
		}
		JSONObject offer=getOfferById(offerId);
		String solrUrl=getSolrReadUrl()+"?wt=json&fl=offers,title&q=id:"+productId+" AND apiKey:"+tenantId;
		String rawResult=solrRestTemplateProxy.getRestTemplate().getForObject(solrUrl, String.class);
		JSONObject jsonResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)(((JSONObject)jsonResult.get("response")).get("docs"));
		if(docs.size()==0){
			throw new NoSuchProductException("No such product");
		}
		JSONObject jsonDoc=(JSONObject)docs.get(0);
		if(jsonDoc.get("offers")==null){
			log.warn("No offers found in product:{}",productId);
			throw new IllegalStateException("No offers found in product");
		}
		List<String> offerIds=(List<String>)(jsonDoc.get("offers"));
		if(!offerIds.contains(offerId)){
			log.warn("Offer {} NOT found in product {}",offerId, productId);
			throw new NoSuchOfferException("No such offer in that product");
		}
		JSONObject cc=getCreditCardById(creditCardId);
		String transactionId=UUID.randomUUID().toString();

		JSONObject orderResponse=createSimplePaymentsWithSavedCreditCard(customerId, (String)cc.get("braintreeId2"), (String)offer.get("price"), (String)((List)jsonDoc.get("title")).get(0), productId+":"+offerId, transactionId);
		log.info("Purchase completed for customer:{}:{}",customerId,orderResponse);
		orderResponse.put("customerId", customerId);
		orderResponse.put("tenantId", tenantId);
		orderResponse.put("offer",offer);
		orderResponse.put("productId",productId);
		orderResponse.put("offerId",offerId);
		orderResponse.put("creditCardid",creditCardId);
		orderResponse.put("braintreeId2",cc.get("braintreeId2"));
		databaseRestTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/purchase", JSONHelper.marshall(orderResponse));
		
		String url=getEntitlementCreationUrlTemplate()
				.replace("{tenantId}",tenantId)
				.replace("{customerId}",customerId)
				.replace("{productId}",productId)
				.replace("{offerId}",offerId);
		entitlementRestTemplateProxy.getRestTemplate().postForLocation(url, orderResponse);
		producer.publish(getPurchaseTopic(), orderResponse.toJSONString());
		return orderResponse;
	}

	private String truncate(String e){
		if(e==null){
			return null;
		}else if(e.length()<17){
			return e;
		}else{
			return e.substring(e.length()-17);
		}
	}
	 private JSONObject createSimplePaymentsWithSavedCreditCard(String customerId, String savedCreditCardId,String payedAmount,String description, String item, String transactionId) {
	    	JSONObject o= new JSONObject();
	    	o.put("description", description);
	    	o.put("item", item);
	    	o.put("transactionId", transactionId);
	    	TransactionRequest request = new TransactionRequest()
	        .customerId(customerId)
	        .amount(new BigDecimal(payedAmount))
	        .purchaseOrderNumber(truncate(item)).orderId(truncate(transactionId))
	        .deviceData(o.toString()).options().submitForSettlement(true).done()
	        .creditCard().done();
	    	Result<Transaction> result = getGateway().transaction().sale(request);
	    	if(!result.isSuccess()){
	        	log.info("Could not execute transaction. Customer: {}, Item:{}, Message:{}",customerId,item,result.getMessage());
	        	throw new IllegalStateException(result.getMessage());
	    	}else{
	        	log.info("Transaction successful for user:{}. Transaction ID:{}. Item:{}",customerId,transactionId,item);
	        	Transaction  cc=result.getTarget();
	        	o.put("braintreeId", result.getTarget().getId());
	        	o.put("braintreeOrderId", result.getTarget().getOrderId());
	        	return o;
	        }
	   }
	public JSONObject getOfferById(String offerId) throws NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/offer/"+offerId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchOfferException("No Such Offer");
	}

	private JSONObject getCreditCardById(String creditCardId) throws NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/creditcard/"+creditCardId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			return unmarshalled;
		}
		throw new NoSuchOfferException("No Such Offer");
	}

	public JSONObject findAll(Integer pageSize,Integer pageNumber, String sortOrder, String sortField) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> purchases=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/purchase/search/findByCustomerIdAndTenantId?customerId="+customerId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(purchases.getBody());
		metadataProvider.populateMetadataIntoItem((List<JSONObject>)unmarshalled.get("result"), "productId");
		return unmarshalled;
	}

	@Override
	public JSONObject getPurchaseById(String purchaseId)throws NoSuchOfferException, NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> purchases=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/purchase/"+purchaseId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(purchases.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			metadataProvider.populateMetadataIntoItem(unmarshalled, "productId");
			return unmarshalled;
		}
		throw new NoSuchOfferException("No Such purchase");
	}

}
