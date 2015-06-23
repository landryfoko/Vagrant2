package com.libertas.vipaas.services.review;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
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
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRatingException;
import com.libertas.vipaas.common.exceptions.NoSuchReviewException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("review")
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private RestTemplateProxy restTemplateProxy;

	//@Autowired
	private ProducerTemplate producer;
	@Autowired
	private MetadataProvider metadataProvider;
	private String databaseServiceName;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}
	@Override
	public JSONObject createReview( String productId, 	JSONObject metadata) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		boolean exists=existsProductById(productId);
		if(!exists){
			throw new NoSuchProductException("No such product");
		}
		metadata.remove("id");
		JSONObject review= new JSONObject(metadata);
		review.put("productId", productId);
		review.put("customerId", customerId);
		review.put("tenantId", tenantId);
		review.put("id", UUID.randomUUID().toString());;
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/review", JSONHelper.marshall(review));
		return metadataProvider.filter(review);
	}
	
	
	
	@Override
	public JSONObject getReviewById(String reviewId) throws NoSuchReviewException {
		JSONObject rating=getReviewByInternalId(reviewId);
		metadataProvider.filter(rating);
		return rating;
	}

	public JSONObject getReviewByInternalId(String reviewId) throws NoSuchReviewException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> review=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/review/"+reviewId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(review.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchReviewException("No Such Review");
	}

	@Override
	public JSONObject findAll(String productId, Integer pageSize,	Integer pageNumber, String sortOrder, String sortField) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> reviews=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/review/search/findByProductIdAndAndTenantId?productId="+productId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(reviews.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;
	}

	@Override
	public void updateReview(String reviewId, JSONObject metadata) throws NoSuchReviewException {
		JSONObject oldReview=getReviewByInternalId(reviewId);
		if(oldReview==null ){
			throw new NoSuchReviewException("No such review");
		}
		metadata.remove("id");
		metadata.remove("deviceId");
		metadata.remove("tenantId");
		metadata.remove("customerId");
		oldReview.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/review/"+oldReview.get("id"), JSONHelper.marshall(oldReview));

	}
	
	

	@Override
	public void deleteReviewById( String reviewId) throws NoSuchReviewException {
		JSONObject device=getReviewByInternalId(reviewId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		device.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		device.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/review/"+device.get("id"), JSONHelper.marshall(device));
	}


	@Cacheable(value = { "product-exists" })
	private boolean existsProductById(String productId){
		return metadataProvider.existsProduct(productId);
	}


}
