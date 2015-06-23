package com.libertas.vipaas.services.entitlement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateDeviceException;
import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("entitlement")
public class EntitlementServiceImpl implements EntitlementService {
	@Autowired
	private MetadataProvider metadataProvider;

	@Autowired
	RestTemplateProxy databaseRestTemplateProxy;

	private String databaseServiceName;
	private String solrReadUrl;
	@Autowired
	RestTemplateProxy solrRestTemplateProxy;

	//@Autowired
	private ProducerTemplate producer;
	public String getSolrReadUrl() {
		return solrReadUrl;
	}

	public void setSolrReadUrl(String solrReadUrl) {
		this.solrReadUrl = solrReadUrl;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public void createEntitlement( String productId,String offerId,	JSONObject metadata) throws NoSuchProductException, NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		boolean exists=existsProductById(productId);
		if(!exists){
			throw new NoSuchProductException("No such product");
		}
		JSONObject offer=findOfferById(offerId);
		metadata.remove("id");
		JSONObject entitlement= new JSONObject();
		entitlement.put("productId", productId);
		entitlement.put("tenantId", tenantId);
		entitlement.put("offerId", offerId);
		entitlement.put("purchase", metadata);
		entitlement.put("customerId", metadata.get("customerId"));
		entitlement.put("id", UUID.randomUUID().toString());;
		entitlement.put("creationDateMillis", System.currentTimeMillis());
		databaseRestTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/entitlement", JSONHelper.marshall(entitlement));
	}
	private JSONObject findOfferById(String offerId) throws NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> offers=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/offer/"+offerId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(offers.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchOfferException("No such offer");
	}
	@Override
	public JSONObject findById(String entitlementId) throws NoSuchEntitlementException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> entitlements=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/entitlement/"+entitlementId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(entitlements.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			return unmarshalled;
		}
		throw new NoSuchEntitlementException("No Such Entitlement");
	}

	@Override
	public JSONObject findAll(String customerId, Integer pageSize,	Integer pageNumber, Collection<String> productIds, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);

		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String query=null;
		if(productIds!=null && productIds.size()>0){
			query="/entitlement/search/findByCustomerIdAndTenantIdAndProductId?customerId="+customerId+"&tenantId="+tenantId+"&productId="+StringUtils.join(productIds,",")+"&size="+pageSize+"&page="+pageNumber+sort;
		}else{
			query="/entitlement/search/findByCustomerIdAndTenantId?customerId="+customerId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber;

		}
		ResponseEntity<JSONObject> device=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+query,HttpMethod.GET,entity, JSONObject.class);
		return JSONHelper.unmarshall(device.getBody());
	}

	@Override
	public void updateEntitlement(String entitlementId, JSONObject metadata) throws NoSuchEntitlementException {
		JSONObject entitlement=findById(entitlementId);
		metadata.remove("id");
		metadata.remove("customerId");
		metadata.remove("tenantId");
		metadata.remove("offerId");
		metadata.remove("type");
		entitlement.putAll(metadata);
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/entitlement/"+entitlementId, JSONHelper.marshall(entitlement));
	}

	@Override
	public void disableEntitlement(String entitlementId, Long disableDateMillis) throws NoSuchEntitlementException {
		JSONObject entitlement=findById(entitlementId);
		entitlement.put("disableDateMillis",disableDateMillis);
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/entitlement/"+entitlementId, JSONHelper.marshall(entitlement));
	}

	@Cacheable(value = { "product-exists" })
	private boolean existsProductById(String productId){
		return metadataProvider.existsProduct(productId);
	}
	private List<String>getProductSubscriptions(String productId, String tenantId){
		String url=getSolrReadUrl()+"?wt=json&fl=subscriptions&q=apiKey:"+tenantId+" AND id:"+productId;
		String rawResult=solrRestTemplateProxy.getRestTemplate().getForObject(url, String.class);

		JSONObject jsonRawResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)((JSONObject)jsonRawResult.get("response")).get("docs");
		if(docs.size()!=0){
			List<String> subscriptions=(List<String>)((JSONObject)docs.get(0)).get("subscriptions");
			if(subscriptions!=null){
				return subscriptions;
			}
		}
		return new ArrayList<String>();
	}

	public JSONObject checkEntitlement(JSONObject entitlements) {
		List<JSONObject>list=(List<JSONObject>)entitlements.get("result");
		if(list!=null && list.size()!=0){
			for(JSONObject entitlement:list){
				JSONObject purchase=(JSONObject)entitlement.get("purchase");
				JSONObject offer=(JSONObject)purchase.get("offer");
				Long creationDateMillis=(Long)entitlement.get("creationDateMillis");
				Long entitlementDurationMillis=(Long)offer.get("entitlementDurationMillis");
				if(creationDateMillis+entitlementDurationMillis>System.currentTimeMillis() && (entitlement.get("disableDateMillis")==null|| ((Long) entitlement.get("disableDateMillis"))>System.currentTimeMillis() )){
					return  entitlement;
				}
			}
		}
		return null;
	}

	@Override
	public JSONObject findValidOne(String productId) throws NoSuchEntitlementException, NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		boolean exists=existsProductById(productId);
		if(!exists){
			throw new NoSuchProductException("No such product");
		}
		JSONObject entitlements=findAll(customerId,100, 0, Arrays.asList(productId), null,null);
		JSONObject check=checkEntitlement(entitlements);
		if(check!=null){
			return check;
		}
		List<String> subscriptions=getProductSubscriptions(productId, tenantId);
		if(subscriptions!=null && subscriptions.size()>0){
			entitlements=findAll(customerId,100, 0, subscriptions,null,null);
			check=checkEntitlement(entitlements);
			if(check!=null){
				return check;
			}
		}
		throw new NoSuchEntitlementException("No valid entitlement found on product");
	}

}
