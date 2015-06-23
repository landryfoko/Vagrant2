package com.libertas.vipaas.common.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
//import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletContextAware;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateProductException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Component
@ConfigurationProperties("product")
@Slf4j
public class MetadataProvider{

	@Autowired
	private HttpServletRequest httpServletRequest;
	private String solrReadUrl;
	private RestTemplate solrReadRestTemplate;
	@Setter @Getter
	private String databaseServiceName="database";
	@Autowired @Setter @Getter
	private RestTemplateProxy databaseRestemplateProxy;

	@Autowired
//	public MetadataProvider(RibbonInterceptor ribbonInterceptor){
//		solrReadRestTemplate = new RestTemplate();
//		solrReadRestTemplate.setInterceptors(Arrays.asList(ribbonInterceptor));
//	}
	
	public MetadataProvider(LoadBalancerInterceptor loadBalancerInterceptor){
		solrReadRestTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> list = new ArrayList<>();
		list.add(loadBalancerInterceptor);
		solrReadRestTemplate.setInterceptors(list);
	}

	public String getSolrReadUrl() {
		return solrReadUrl;
	}


	public void setSolrReadUrl(String solrReadUrl) {
		this.solrReadUrl = solrReadUrl;
	}

	public List<JSONObject> findProductFieldsByProductId(String productId,String ... fieldName) throws NoSuchProductException{
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String url=getSolrReadUrl()+"/?rows=100&wt=json&fl="+StringUtils.join(fieldName,",")+"&q=apiKey:"+tenantId+" AND id:"+productId;
		String text=solrReadRestTemplate.getForObject(url, String.class);
		JSONObject json=(JSONObject)JSONValue.parse(text);
		List docs=(List)((JSONObject)json.get("response")).get("docs");
		if(docs.size()==0){
            throw new NoSuchProductException("No such product");
		}
		return docs;
	}

	public JSONObject populateMetadataIntoItem(JSONObject value, String key) throws NoSuchProductException{
		List<JSONObject>json=populateMetadataIntoItem(Arrays.asList(value),key);
		if(json.size()==0){
            throw new NoSuchProductException("No such product");
		}
		return  json.get(0);
	}
	public List<JSONObject> populateMetadata(List<String> fieldValues, String fieldName){
		if(fieldValues==null || fieldValues.size()==0){
			return new ArrayList<JSONObject>();
		}
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		final String query=StringUtils.join(fieldValues," OR "+fieldName+":");
		String url=getSolrReadUrl()+"/?rows=100&wt=json&fl=blob&q="+fieldName+":"+query+" AND apiKey:"+tenantId;
		String text=solrReadRestTemplate.getForObject(url, String.class);
		JSONObject json=(JSONObject)JSONValue.parse(text);
		List docs=(List)((JSONObject)json.get("response")).get("docs");
		final List<JSONObject> result= new ArrayList<JSONObject>(docs.size());
		for(final Object doc:docs){
			final JSONObject movie=(JSONObject)doc;
			result.add((JSONObject)JSONValue.parse(((List)movie.get("blob")).get(0).toString()));
		}
		return  result;
	}

	public List<JSONObject> populateMetadataIntoItem(List<JSONObject> items, String fieldName){
		if(items==null || items.size()==0){
			return items;
		}
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		List<String>ids= new ArrayList<String>();
		for(JSONObject item:items){
			ids.add((String)item.get(fieldName));
		}
		final String query=StringUtils.join(ids," OR id:");
		String url=getSolrReadUrl()+"/?rows=100&wt=json&fl=blob&q=(id:"+query+") AND apiKey:"+tenantId;
		String text=solrReadRestTemplate.getForObject(url, String.class);
		JSONObject json=(JSONObject)JSONValue.parse(text);
		List docs=(List)((JSONObject)json.get("response")).get("docs");
		final Map<String,JSONObject> result= new HashMap<String,JSONObject>(docs.size());
		for(final Object doc:docs){
			final JSONObject movie=(JSONObject)doc;
			JSONObject blob=(JSONObject)JSONValue.parse(((List)movie.get("blob")).get(0).toString());
			blob.put("productId", blob.get("id"));
			blob.remove("id");
			result.put((String)blob.get("productId"),blob);
		}
		for(JSONObject item:items){
			if(result.get(item.get(fieldName))!=null){
				item.put("product",result.get(item.get(fieldName)));
			}
		}
		return  items;
	}
	public boolean existsProduct(String productId){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String url=getSolrReadUrl()+"/?rows=1&wt=json&fl=id"+"&q=apiKey:"+tenantId+" AND id:"+productId;
		String text=solrReadRestTemplate.getForObject(url, String.class);
		JSONObject json=(JSONObject)JSONValue.parse(text);
		List docs=(List)((JSONObject)json.get("response")).get("docs");
		return docs.size()!=0;
	}

	public void checkProductExists(String productId) throws NoSuchProductException{
		boolean existsProduct=existsProduct(productId);
		if(!existsProduct){
			throw new NoSuchProductException("No such product");
		}
	}

	public void checkProductNotExists(String productId) throws DuplicateProductException{
		boolean existsProduct=existsProduct(productId);
		if(existsProduct){
			throw new DuplicateProductException("Product already exists");
		}
	}

	public JSONObject validateProductReady4Purchase(String productId, JSONObject solrResult) throws NoSuchProductException{
		List docs=(List)(((JSONObject)solrResult.get("response")).get("docs"));
		if(docs.size()==0){
			throw new NoSuchProductException("No such product");
		}
		JSONObject jsonDoc=(JSONObject)docs.get(0);
		if(jsonDoc.get("offers")==null){
			log.warn("No offers found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No offers found in product");
		}
		if(jsonDoc.get("details")==null){
			log.warn("No details found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No details found in product");
		}
		if(jsonDoc.get("videos")==null){
			log.warn("No videos found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No videos found in product");
		}
		return jsonDoc;
	}

	protected List<JSONObject> getVideosWithValidUrl(String productId, JSONObject product){
		List<String> videosStr=(List<String>)product.get("videos");
		List<JSONObject> videos=new ArrayList<JSONObject>(videosStr.size());
		for(String videoStr:videosStr){
			JSONObject video=(JSONObject)JSONValue.parse(videoStr);
			if(video.get("url")!=null &&  StringUtils.isNotBlank(video.get("url").toString())){
				videos.add(video);
			}else{
				log.info("Video has no URL in product {}: Ignoring it.{}",productId,video);
			}
		}
		if(videos.size()==0){
			log.warn("No videos found in product:{} with valid URL. Cannot compute its product details.",productId);
			throw new IllegalStateException("No videos found in product");
		}
		return videos;
	}

	public JSONObject getProductDetails(String productId,JSONObject offer) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String solrUrl=getSolrReadUrl()+"?wt=json&fl=details,offers,videos&q=id:"+productId+" AND apiKey:"+tenantId;
		String rawResult=solrReadRestTemplate.getForObject(solrUrl, String.class);
		JSONObject jsonDoc=validateProductReady4Purchase(productId, (JSONObject)JSONValue.parse(rawResult));
		List<JSONObject> videos=getVideosWithValidUrl(productId, jsonDoc);
		JSONObject details=(JSONObject)JSONValue.parse((((List<String>)jsonDoc.get("details")).get(0)));
		JSONArray mediaList= new JSONArray();
		details.put("mediaList", mediaList);
		String regex=(String)offer.get("regex").toString().toLowerCase();
		for(JSONObject video:videos){
			String targetDevice=(String)video.get("targetDevice");
			String aspectRatio=(String)video.get("aspectRatio");
			String screenFormat=(String)video.get("screenFormat");
			String url=(String)video.get("url");
			if(StringUtils.isEmpty(url) || StringUtils.isEmpty(aspectRatio) || StringUtils.isEmpty(targetDevice)|| StringUtils.isEmpty(screenFormat)){
				log.info("Asset with component Id {} in product {} does not have valid url, aspectRatio, targetDevice, or screenFormat. Cannot add it to product details.",video.get("componentId"),productId);
			}else if(Pattern.matches(regex,url)  ||
					   Pattern.matches(regex,aspectRatio.toLowerCase())  ||
					   Pattern.matches(regex,targetDevice.toLowerCase()) ||
					   Pattern.matches(regex,screenFormat.toLowerCase()) ){
						mediaList.add(video);
			}
		}
		return details;
	}

	   @Cacheable(value = "tenants")
	    public JSONObject getTenantById(final String tenantId)  {
	        log.info("Fetching tenant with Id:{}", tenantId);
	        final HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "application/json");
	        HttpEntity<String> entity = new HttpEntity<String>(headers);
	        ResponseEntity<JSONObject> tenant = databaseRestemplateProxy.getRestTemplate().exchange(getDatabaseServiceName() + "/tenant/" + tenantId + "?apiKey=" + tenantId, HttpMethod.GET, entity,
	                JSONObject.class);
	        if (tenant == null || tenant.getBody() == null)
	        	throw new IllegalArgumentException("No user with given tenant Id:"+tenantId);
	        final JSONObject unmarshalled = JSONHelper.unmarshall(tenant.getBody());
	        if (unmarshalled != null && unmarshalled.size() > 0) {
	            log.info("Tenant found in filter:{}", unmarshalled);
	            return unmarshalled;
	        }
	        throw new IllegalArgumentException("No user with given tenant Id:"+tenantId);
	    }


	public  JSONObject filter(JSONObject item,String ...additionalFields){
		if(item==null){
			return null;
		}
		item.keySet().removeAll(Arrays.asList("customerId","tenantId","password"));
		if(additionalFields!=null){
			item.keySet().removeAll(Arrays.asList(additionalFields));
		}
		String url=httpServletRequest.getRequestURI();
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		JSONObject tenant=getTenantById((String)credentials.get("tenantId"));
		if(tenant.get("filter")!=null){
			Map<String,List<String>> spec=(Map<String,List<String>>)tenant.get("filter");
			for(String key:spec.keySet()){
				if(Pattern.matches(key, url)){
					List<String>allowedFields=spec.get(key);
					return (JSONObject)filterDeeply(item, allowedFields);
				};
			}
		}
		return item;
	}

	private Object filterDeeply(Object item, List<String> allowedFields){
		if(item instanceof Map){
			JSONObject json=(JSONObject)item;
			json.keySet().retainAll(allowedFields);
			for(Object value:json.values()){
				filterDeeply(value, allowedFields);
			}
			return json;
		}
		else if(item instanceof List){
			List json=(List)item;
			for(Object value:json){
				filterDeeply(value, allowedFields);
			}
			return json;
		}
		else{
			return item;
		}
	}
	public  List<JSONObject> filter(List<JSONObject> items, String ...additionalFields){
		if(items==null){
			return null;
		}
		for(JSONObject item:items){
			filter(item,additionalFields);
		}
		return items;
	}
}
