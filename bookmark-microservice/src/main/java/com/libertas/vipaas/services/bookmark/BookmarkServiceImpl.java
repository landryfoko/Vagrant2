package com.libertas.vipaas.services.bookmark;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.exceptions.NoSuchBookmarkException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("bookmark")
@Slf4j
public class BookmarkServiceImpl implements BookmarkService {


	@Autowired
	RestTemplate restTemplate;
	private String databaseServiceName;
	@Autowired
	private MetadataProvider metadataProvider;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public JSONObject createBookmark( String productId,	JSONObject metadata) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		metadataProvider.checkProductExists(productId);
		metadata.remove("id");
		JSONObject bookmark= new JSONObject(metadata);
		bookmark.put("productId", productId);
		bookmark.put("customerId", customerId);
		bookmark.put("tenantId", tenantId);
		bookmark.put("id", UUID.randomUUID().toString());
		restTemplate.postForLocation(getDatabaseServiceName()+"/bookmark", JSONHelper.marshall(bookmark));
		return metadataProvider.filter(bookmark);
	}

	@Override
	public JSONObject getBookmarkById(String bookmarkId) throws NoSuchBookmarkException, NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=restTemplate.exchange(getDatabaseServiceName()+"/bookmark/"+bookmarkId,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			unmarshalled=metadataProvider.populateMetadataIntoItem(unmarshalled, "productId");
			metadataProvider.filter(unmarshalled);
			return unmarshalled;
		}
		throw new NoSuchBookmarkException("No Such Bookmark");
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
		ResponseEntity<JSONObject> bookmarks=restTemplate.exchange(getDatabaseServiceName()+"/bookmark/search/findByCustomerIdAndTenantId?customerId="+customerId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(bookmarks.getBody());
		metadataProvider.populateMetadataIntoItem((List<JSONObject>)unmarshalled.get("result"),"productId");
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;
	}

	@Override
	public void updateBookmark(String bookmarkId, JSONObject metadata) throws NoSuchBookmarkException, NoSuchProductException {
		JSONObject bookmark=getBookmarkById(bookmarkId);
		metadata.remove("id");
		metadata.remove("tenantId");
		bookmark.putAll(metadata);
		restTemplate.put(getDatabaseServiceName()+"/bookmark/"+bookmarkId, JSONHelper.marshall(bookmark));
	}

	@Override
	public void deleteBookmarkById( String bookmarkId) throws NoSuchBookmarkException, NoSuchProductException {
		JSONObject device=getBookmarkById(bookmarkId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		device.put("customerId", customerId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		device.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		device.put("deleteDate", new Date().toString());
		restTemplate.put(getDatabaseServiceName()+"/bookmark/"+device.get("id"), JSONHelper.marshall(device));
	}
}
