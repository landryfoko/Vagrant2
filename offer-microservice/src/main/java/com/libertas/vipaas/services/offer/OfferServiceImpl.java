package com.libertas.vipaas.services.offer;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateOfferException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("offer")
public class OfferServiceImpl implements OfferService {
	@Autowired
	MetadataProvider metadataProvider;

	@Autowired
	private RestTemplateProxy restTemplateProxy;

	private String databaseServiceName;
	//@Autowired
	private ProducerTemplate producer;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject createOffer(String name, JSONObject metadata) throws DuplicateOfferException, MissingFieldException  {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		if(StringUtils.isEmpty((String)metadata.get("regex"))){
			throw new MissingFieldException("Missing regex field in request");
		}
		if(StringUtils.isEmpty((String)metadata.get("offerType"))){
			throw new MissingFieldException("Missing offerType field in request");
		}
		if(metadata.get("startDateTimestampMillis")==null){
			throw new MissingFieldException("Missing startDateTimestampMillis field in request");
		}
		if(metadata.get("endDateTimestampMillis")==null){
			throw new MissingFieldException("Missing endDateTimestampMillis field in request");
		}
		if(metadata.get("entitlementDurationMillis")==null){
			throw new MissingFieldException("Missing entitlementDurationMillis field in request");
		}
		if(existsOfferByName(name)){
			throw new DuplicateOfferException("Offer already exists");
		}
		metadata.remove("id");
		JSONObject offer= new JSONObject(metadata);
		offer.put("name", name);
		offer.put("tenantId", tenantId);
		offer.put("id", UUID.randomUUID().toString());
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/offer", JSONHelper.marshall(offer));
		return metadataProvider.filter(offer);
	}

	private JSONObject findByIdInternal(String offerId) throws NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/offer/"+offerId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchOfferException("No Such Offer");
	}
	@Override
	public JSONObject findById(String offerId) throws NoSuchOfferException {
		JSONObject json=findByIdInternal(offerId);
		json=metadataProvider.filter(json);
		return json;
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> offers=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/offer/search/findByTenantId?tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(offers.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;

	}

	@Override
	public void updateOffer(String offerId, JSONObject metadata) throws NoSuchOfferException {
		JSONObject oldOffer=findByIdInternal(offerId);
		if(oldOffer==null ){
			throw new NoSuchOfferException("No such offer");
		}
		metadata.remove("id");
		metadata.remove("tenantId");
		metadata.remove("customerId");
		oldOffer.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/offer/"+oldOffer.get("id"), JSONHelper.marshall(oldOffer));

	}

	@Override
	public void deleteOfferById( String offerId) throws NoSuchOfferException {
		JSONObject offer=findByIdInternal(offerId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		offer.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		offer.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/offer/"+offer.get("id"), JSONHelper.marshall(offer));
	}

	private boolean existsOfferByName(String name){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject offer=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/offer/search/findByNameAndTenantId?name="+name+"&tenantId="+tenantId, JSONObject.class);
		if(offer==null){
			return false;
		}
		return offer!=null && offer.size()>0 && ((List)offer.get("links")).size()>0;
	}

}
