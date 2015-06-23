package com.libertas.vipaas.services.subscriptionpackage;

import java.util.Date;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchSubscriptionPackageException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("subscriptionpackaget")
public class SubscriptionPackageServiceImpl implements SubscriptionPackageService {

	@Autowired
	private RestTemplateProxy restTemplateProxy;

	private String databaseServiceName;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}
	@Override
	public void createSubscriptionPackage(JSONObject metadata) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		metadata.remove("id");
		JSONObject subscriptionPackage= new JSONObject(metadata);
		subscriptionPackage.put("customerId", customerId);
		subscriptionPackage.put("tenantId", tenantId);
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/subscriptionPackage", JSONHelper.marshall(subscriptionPackage));
	}

	@Override
	public JSONObject getSubscriptionPackageById(String subscriptionPackageId) throws NoSuchSubscriptionPackageException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/subscriptionPackage/"+subscriptionPackageId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchSubscriptionPackageException("No Such SubscriptionPackage");
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber) {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");


		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> device=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/subscriptionPackage/search/findByTenantId?tenantId="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		return JSONHelper.unmarshall(device.getBody());

	}

	@Override
	public void updateSubscriptionPackage(String subscriptionPackageId, JSONObject metadata) throws NoSuchSubscriptionPackageException {
		JSONObject oldSubscriptionPackage=getSubscriptionPackageById(subscriptionPackageId);
		if(oldSubscriptionPackage==null ){
			throw new NoSuchSubscriptionPackageException("No such subscriptionPackage");
		}
		metadata.remove("id");
		metadata.remove("deviceId");
		metadata.remove("tenantId");
		metadata.remove("customerId");
		oldSubscriptionPackage.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/subscriptionPackage/"+oldSubscriptionPackage.get("id"), JSONHelper.marshall(oldSubscriptionPackage));

	}

	@Override
	public void deleteSubscriptionPackageById( String subscriptionPackageId) throws NoSuchSubscriptionPackageException {
		JSONObject device=getSubscriptionPackageById(subscriptionPackageId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		device.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		device.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/subscriptionPackage/"+device.get("id"), JSONHelper.marshall(device));
	}

}
