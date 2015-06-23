package com.libertas.vipaas.services.recommendation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateRecommendationException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRecommendationException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("recommendation")
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired
	MetadataProvider metadataProvider;
	@Autowired
	private RestTemplateProxy restTemplateProxy;


	//@Autowired
	private ProducerTemplate producer;
	private String databaseServiceName;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}
	@Override
	public JSONObject createRecommendation( String productId,	List<JSONObject> recommendations) throws  DuplicateRecommendationException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		boolean exists=existsRecommendationByProductId(productId);
		if(exists){
			throw new DuplicateRecommendationException("Recommendation with given name already exists");
		}
		JSONObject recommendation= new JSONObject();
		recommendation.put("productId", productId);
		recommendation.put("tenantId", tenantId);
		recommendation.put("recommendations",recommendations);
		recommendation.put("id", UUID.randomUUID().toString());;
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/recommendation", JSONHelper.marshall(recommendation));
		return metadataProvider.filter(recommendation);
	}

	@Override
	public JSONObject getRecommendationByProductId(String productId) throws NoSuchRecommendationException, NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> recommendations=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/recommendation/search/findByProductIdAndTenantId?productId="+productId+"&tenantId="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(recommendations.getBody());
		log.info("Recommendation received from database:{}",recommendations.getBody());
		List<JSONObject> recs= (List<JSONObject>)((JSONObject)((List<JSONObject>)unmarshalled.get("result")).get(0)).get("recommendations");
		List<JSONObject> result=metadataProvider.populateMetadataIntoItem(recs, "productId");
		metadataProvider.filter(result,"productId");
		return JSONHelper.make("result", result);
	}

	private JSONObject getRecommendationByIdInternal(String recommendationId) throws NoSuchRecommendationException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> recommendations=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/recommendation/"+recommendationId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(recommendations.getBody());
		return unmarshalled;
	}
	public JSONObject getRecommendationById(String recommendationId) throws NoSuchRecommendationException {
		JSONObject recommendation=getRecommendationByIdInternal(recommendationId);
		metadataProvider.filter(recommendation);
		return recommendation;
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String sortOrder, String sortField) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String findByTenantId="/recommendation/search/findByTenantId?tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort;
		ResponseEntity<JSONObject> device=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+findByTenantId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(device.getBody());
		List<JSONObject> recs= (List<JSONObject>)((JSONObject)((List<JSONObject>)unmarshalled.get("result")).get(0)).get("recommendations");
		List<JSONObject> result=metadataProvider.populateMetadataIntoItem(recs, "productId");
		metadataProvider.filter(result,"productId");
		return JSONHelper.make("result", result);

	}

	@Override
	public void updateRecommendation(String recommendationId, JSONObject metadata) throws NoSuchRecommendationException {
		JSONObject promo=getRecommendationByIdInternal(recommendationId);
		metadata.remove("id");
		metadata.remove("tenantId");
		promo.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/recommendation/"+recommendationId, JSONHelper.marshall(promo));
	}


	@Override
	public void deleteRecommendationByProductId( String productId) throws NoSuchRecommendationException {

		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> recommendationWrapper=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/recommendation/search/findByProductIdAndTenantId?productId="+productId+"&tenantId="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		Map embedded=(Map)((Map)recommendationWrapper.getBody()).get("_embedded");
		if(embedded ==null){
			log.info("No such recommendation");
			return;
		}
		List<Map> recommendations=(List<Map>)embedded.get("recommendation");
		if(recommendations.size()==0){
			log.info("No such recommendation");
			return;
		}
		Map recommendation=recommendations.get(0);
		log.info("Recommendation unmarshalled to:{}",recommendation);
		recommendation.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		recommendation.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/recommendation/"+recommendation.get("id"), JSONHelper.marshall(new JSONObject(recommendation)));
	}

	private boolean existsRecommendationByProductId(String productId){
			JSONObject credentials=CredentialsThreadLocal.getCredentials();
			String tenantId=(String)credentials.get("tenantId");
			JSONObject promo=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/recommendation/search/findByProductIdAndTenantId?productId="+productId+"&tenantId="+tenantId, JSONObject.class);
			if(promo==null){
				return false;
			}
			return promo!=null && promo.size()>0 && ((List)promo.get("links")).size()>0;
	}

}
