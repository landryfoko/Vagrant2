package com.libertas.vipaas.services.watchhistory;

import java.util.Date;
import java.util.List;
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
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchWatchHistoryEntryException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("watchhistory")
@Slf4j
public class WatchHistoryServiceImpl implements WatchHistoryService {


	@Autowired
	private MetadataProvider metadataProvider;

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
	public JSONObject createWatchHistoryEntry(String productId, JSONObject metadata) throws NoSuchProductException  {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		metadataProvider.checkProductExists(productId);
		JSONObject entries=getWatchHistoryEntryByProductId(productId);
		if(entries.size()==0){
			metadata.remove("id");
			JSONObject watchhistory= new JSONObject(metadata);
			watchhistory.put("productId", productId);
			watchhistory.put("customerId", customerId);
			watchhistory.put("completed", false);
			watchhistory.put("tenantId", tenantId);
			watchhistory.put("id", UUID.randomUUID().toString());
			watchhistory.put("lastUpdateDate", System.currentTimeMillis());
			restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/watchhistory", JSONHelper.marshall(watchhistory));
			return metadataProvider.filter(watchhistory);
		}else{
			metadata.remove("id");
			metadata.remove("customerId");
			metadata.remove("tenantId");
			entries.put("lastUpdateDate", System.currentTimeMillis());
			entries.putAll(metadata);
			restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/watchhistory/"+entries.get("id"), JSONHelper.marshall(entries));
			return metadataProvider.filter(metadata);
		}
	}

	private JSONObject getWatchHistoryById(String watchhistoryId) throws NoSuchWatchHistoryEntryException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> watchhistorys=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/watchhistory/"+watchhistoryId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(watchhistorys.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId")) && customerId.equals((String)unmarshalled.get("customerId"))){
			metadataProvider.filter(unmarshalled);
			return unmarshalled;
		}
		throw new NoSuchWatchHistoryEntryException("No Such WatchHistory");
	}

	private JSONObject getWatchHistoryEntryByProductId(String productId) {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> watchhistorys=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/watchhistory/search/findByProductIdAndCustomerIdAndTenantIdAndCompleted?productId="+productId+"&tenantId="+tenantId+"&customerId="+customerId+"&completed=false",HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(watchhistorys.getBody());
		metadataProvider.filter(unmarshalled);
		return unmarshalled;
	}
	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String sortOrder, String sortField) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String query="/watchhistory/search/findByTenantIdAndCustomerId?tenantId="+tenantId+"&customerId="+customerId+"&size="+pageSize+"&page="+pageNumber+sort;
		ResponseEntity<JSONObject> watchHistory=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+query,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(watchHistory.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		metadataProvider.populateMetadataIntoItem((List<JSONObject>)unmarshalled.get("result"),"productId");
		return unmarshalled;
	}

	@Override
	public void deleteWatchHistoryEntryById( String watchhistoryId) throws NoSuchWatchHistoryEntryException {
		JSONObject entry = getWatchHistoryById(watchhistoryId);
		//Ensures entry exist and belong to user, who belongs to tenant
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		entry.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		entry.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/watchhistory/"+entry.get("id"), JSONHelper.marshall(entry));
	}



}
