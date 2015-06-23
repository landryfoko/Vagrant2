package com.libertas.vipaas.services.promotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicatePromotionException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchPromotionException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("tenant")
public class PromotionServiceImpl implements PromotionService {
	@Autowired
	MetadataProvider metadataProvider;
	@Autowired
	private RestTemplateProxy databaseRestTemplateProxy;
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
	public JSONObject createPromotion( String name,	JSONObject metadata) throws  DuplicatePromotionException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		boolean exists=existsPromotionByName(name);
		if(exists){
			throw new DuplicatePromotionException("Promotion with given name already exists");
		}
		metadata.remove("id");
		JSONObject promotion= new JSONObject(metadata);
		promotion.put("name", name);
		promotion.put("tenantId", tenantId);
		promotion.put("id", UUID.randomUUID().toString());;
		databaseRestTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/promotion", JSONHelper.marshall(promotion));
		return metadataProvider.filter(promotion);
	}
	private JSONObject getPromotionByIdInternal(String promotionId) throws NoSuchPromotionException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> promotions=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/promotion/"+promotionId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(promotions.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchPromotionException("No Such Promotion");
	}

	@Override
	public JSONObject getPromotionById(String promotionId) throws NoSuchPromotionException {
		JSONObject promotion=getPromotionByIdInternal(promotionId);
		metadataProvider.filter(promotion);
		return promotion;
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String tag, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String findByTenantId="/promotion/search/findByTenantId?tenantId="+tenantId+sort;
		String findByTagsAndTenantId="/promotion/search/findByTagsAndTenantId?tenantId="+tenantId+"&tag="+tag+sort;
		ResponseEntity<JSONObject> promotions=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+(StringUtils.isEmpty(tag)?findByTenantId:findByTagsAndTenantId),HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(promotions.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;
	}

	@Override
	public void updatePromotion(String promotionId, JSONObject metadata) throws NoSuchPromotionException {
		JSONObject promo=getPromotionByIdInternal(promotionId);
		metadata.remove("id");
		metadata.remove("tenantId");
		promo.putAll(metadata);
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/promotion/"+promotionId, JSONHelper.marshall(promo));
	}
	@Override
	public void tagPromotion(String promotionId, List<String> tags) throws NoSuchPromotionException, MissingFieldException {
		if(tags==null || tags.size()==0) {
			throw new MissingFieldException("Missing tag list in request body");
		}
		JSONObject promo=getPromotionByIdInternal(promotionId);
		promo.putIfAbsent("tags", new ArrayList<String>());
		((List)promo.get("tags")).addAll(tags);
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/promotion/"+promotionId, JSONHelper.marshall(promo));
	}
	@Override
	public void untagPromotion(String promotionId, List<String> tags) throws NoSuchPromotionException, MissingFieldException {
		if(tags==null || tags.size()==0) {
			throw new MissingFieldException("Missing tag list in request body");
		}
		JSONObject promo=getPromotionByIdInternal(promotionId);
		promo.putIfAbsent("tags", new ArrayList<String>());
		((List)promo.get("tags")).removeAll(tags);
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/promotion/"+promotionId, JSONHelper.marshall(promo));
	}

	@Override
	public void deletePromotionById( String promotionId) throws NoSuchPromotionException {
		JSONObject promotion=getPromotionByIdInternal(promotionId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		promotion.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		promotion.put("deleteDate", new Date().toString());
		databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/promotion/"+promotion.get("id"), JSONHelper.marshall(promotion));
	}

	private boolean existsPromotionByName(String name){
			JSONObject credentials=CredentialsThreadLocal.getCredentials();
			String tenantId=(String)credentials.get("tenantId");
			JSONObject promo=databaseRestTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/promotion/search/findByNameAndTenantId?name="+name+"&tenantId="+tenantId, JSONObject.class);
			if(promo==null){
				return false;
			}
			return promo!=null && promo.size()>0 && ((List)promo.get("links")).size()>0;
	}

}
