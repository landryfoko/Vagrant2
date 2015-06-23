package com.libertas.vipaas.services.recommendation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRecommendationException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.metadata.MetadataProvider;

import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("rotten-tomatoes")
@Slf4j
public class RottenTomatoesRecommendationServiceImpl implements RottenTomatoesRecommendationService {


	@Autowired
	RestTemplate databaseRestTemplate;
	@Autowired
	RestTemplate solrReadRestTemplate;

	@Autowired
	MetadataProvider metadataProvider;

	private String databaseServiceName;
	private String applicationName;
	private String applicationKey;
	private String searchQueryUrlTemplate;
	private String similarQueryUrlTemplate;
	private String solrReadUrl;
	private String rottenTomatoesIdField;
	private boolean manualRecommendationFallbackEnabled;

	public boolean isManualRecommendationFallbackEnabled() {
		return manualRecommendationFallbackEnabled;
	}
	public void setManualRecommendationFallbackEnabled(boolean manualRecommendationFallbackEnabled) {
		this.manualRecommendationFallbackEnabled = manualRecommendationFallbackEnabled;
	}
	public String getApplicationKey() {
		return applicationKey;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public String getDatabaseServiceName() {
		return databaseServiceName;
	}
	private JSONObject getManualRecommendationByProductId(final String productId) throws NoSuchRecommendationException{
		if(!isManualRecommendationFallbackEnabled()){
			return JSONHelper.make("result", new JSONObject());
		}
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> recommendations=databaseRestTemplate.exchange(getDatabaseServiceName()+"/recommendation/search/findByProductIdAndTenantId?productId="+productId+"&tenantId="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(recommendations.getBody());
		log.info("Recommendation received from database:{}",recommendations.getBody());
		if(unmarshalled.get("result")==null){
			return JSONHelper.make("result", new JSONObject());
		}
		List<String> ids= (List<String>)((JSONObject)((List<JSONObject>)unmarshalled.get("result")).get(0)).get("products");
		List<JSONObject> result=metadataProvider.populateMetadata(ids, "id");
		return JSONHelper.make("result", result);
	}
	@Override
	public JSONObject getRecommendationByProductId(final String productId) throws NoSuchRecommendationException, NoSuchProductException {
		final JSONObject credentials=CredentialsThreadLocal.getCredentials();
		final String tenantId=(String)credentials.get("tenantId");
		List<JSONObject> docs=metadataProvider.findProductFieldsByProductId(productId,getRottenTomatoesIdField());
		final List<Object> rottenTomatoesIds=(List<Object>)((JSONObject)docs.get(0)).get(getRottenTomatoesIdField());
		if(rottenTomatoesIds==null || rottenTomatoesIds.size()==0){
			log.info("No RT Id found in product:{}. Return manually configured Recommendations.",productId);
			return getManualRecommendationByProductId(productId);
		}
		String rottenTomatoesId=rottenTomatoesIds.get(0) instanceof Long?((Long)rottenTomatoesIds.get(0)).toString():rottenTomatoesIds.get(0).toString();
		String url=getSimilarQueryUrlTemplate().replace("{apiKey}", getApplicationKey()).replace("{rottenTomatoesProductId}", rottenTomatoesId);
		//Get Recommendation from RT
		String text=solrReadRestTemplate.getForObject(url, String.class);
		JSONObject json=(JSONObject)JSONValue.parse(text);
		docs=(List)json.get("movies");
		if(docs ==null || docs.size()==0){
			log.info("RT product {} not  found. Return manually configured Recommendations.",rottenTomatoesId);
			return getManualRecommendationByProductId(productId);
		}
		final List<String> ids= new ArrayList<String>(docs.size());
		for(final Object doc:docs){
			final JSONObject movie=(JSONObject)doc;
			ids.add((String)movie.get("id"));
		}
		List<JSONObject> result=metadataProvider.populateMetadata(ids, getRottenTomatoesIdField());
		if(result.size()==0){
			return getManualRecommendationByProductId(productId);
		}
		metadataProvider.filter(result);
		return JSONHelper.make("result", result);
	}
	public String getRottenTomatoesIdField() {
		return rottenTomatoesIdField;
	}
	public String getSearchQueryUrlTemplate() {
		return searchQueryUrlTemplate;
	}
	public String getSimilarQueryUrlTemplate() {
		return similarQueryUrlTemplate;
	}
	public String getSolrReadUrl() {
		return solrReadUrl;
	}
	public void setApplicationKey(final String applicationKey) {
		this.applicationKey = applicationKey;
	}
	public void setApplicationName(final String applicationName) {
		this.applicationName = applicationName;
	}
	public void setDatabaseServiceName(final String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	public void setRottenTomatoesIdField(final String rottenTomatoesIdField) {
		this.rottenTomatoesIdField = rottenTomatoesIdField;
	}

	public void setSearchQueryUrlTemplate(final String searchQueryUrlTemplate) {
		this.searchQueryUrlTemplate = searchQueryUrlTemplate;
	}


	public void setSimilarQueryUrlTemplate(final String similarQueryUrlTemplate) {
		this.similarQueryUrlTemplate = similarQueryUrlTemplate;
	}

	public void setSolrReadUrl(final String solrReadUrl) {
		this.solrReadUrl = solrReadUrl;
	}



}
