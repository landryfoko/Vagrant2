package com.libertas.vipaas.services.tenant;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.AuthenticationException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.security.SecurityUtils;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@SuppressWarnings("unchecked")
@ConfigurationProperties("tenant")
public class TenantServiceImpl implements TenantService {


	private String databaseServiceName;
	private String createTenantTopic;
	private String updateTenantTopic;
	private String addAdministratorTopic;
	private String removeAdministratorTopic;
	private String deleteTenantTopic;
	//@Autowired
	private ProducerTemplate producer;
	@Autowired
	RestTemplateProxy restTemplateProxy;


	public String getDeleteTenantTopic() {
		return deleteTenantTopic;
	}

	public void setDeleteTenantTopic(String deleteTenantTopic) {
		this.deleteTenantTopic = deleteTenantTopic;
	}

	public String getAddAdministratorTopic() {
		return addAdministratorTopic;
	}

	public void setAddAdministratorTopic(String addAdministratorTopic) {
		this.addAdministratorTopic = addAdministratorTopic;
	}

	public String getRemoveAdministratorTopic() {
		return removeAdministratorTopic;
	}

	public void setRemoveAdministratorTopic(String removeAdministratorTopic) {
		this.removeAdministratorTopic = removeAdministratorTopic;
	}

	public String getCreateTenantTopic() {
		return createTenantTopic;
	}

	public void setCreateTenantTopic(String createTenantTopic) {
		this.createTenantTopic = createTenantTopic;
	}

	public String getUpdateTenantTopic() {
		return updateTenantTopic;
	}

	public void setUpdateTenantTopic(String updateTenantTopic) {
		this.updateTenantTopic = updateTenantTopic;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public JSONObject createTenant(String email, String password,JSONObject tenant) throws DuplicateUserException, AuthenticationException, MissingFieldException {
		String tenantId=UUID.randomUUID().toString();
		if(StringUtils.isEmpty((String)tenant.get("serviceName"))){
			throw new MissingFieldException("Missing serviceName in request body");
		}
		JSONObject user = new JSONObject();
		user.put("email", email);
		user.put("password", SecurityUtils.hash(password));
		user.put("tenantId", tenantId);
		user.put("id", UUID.randomUUID().toString());
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/customer", JSONHelper.marshall(user));
		tenant.put("ownerId", user.get("id"));
		tenant.put("admins", Arrays.asList(user.get("id")));
		tenant.put("id", tenantId);
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/tenant", JSONHelper.marshall(tenant));
		producer.publish(getCreateTenantTopic(), tenant.toJSONString());
		return tenant;
	}

	@CacheEvict("tenants")
	private JSONObject updateTenantInternal(String tenantId, JSONObject tenant) throws NoSuchUserException {
		JSONObject oldTenant=getTenantById(tenantId);
		tenant.remove("id");
		tenant.remove("tenantId");
		tenant.remove("password");
		oldTenant.putAll(tenant);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/tenant/"+tenantId, JSONHelper.marshall(oldTenant));
		producer.publish(getUpdateTenantTopic(), tenant.toJSONString());
		return oldTenant;
	}
	@Override
	public void updateTenant(String tenantId, JSONObject tenant) throws NoSuchUserException {
		JSONObject updatedTenant=updateTenantInternal(tenantId, tenant);
		producer.publish(getUpdateTenantTopic(), updatedTenant.toJSONString());
	}

	@Override
	@Cacheable("tenants")
	public JSONObject getTenantById(String tenantId) throws NoSuchUserException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> tenant=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/tenant/"+tenantId,HttpMethod.GET,entity, JSONObject.class);
		if(tenant==null || tenant.getBody()==null){
			throw new NoSuchUserException("No user with given Id");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(tenant.getBody());

		if(unmarshalled!=null && unmarshalled.size()>0 ){
			return unmarshalled;
		};
		throw new NoSuchUserException("No user with given Id");
	}

	@Override
	public void addAdministrators(String tenantId, List<String> emails) throws NoSuchUserException{
		JSONObject tenant=getTenantById(tenantId);
		List<String> admins=(List<String>)tenant.get("admins");
		if(admins==null){
			admins= new ArrayList<String>();
		}
		admins.addAll(emails);
		tenant.put("admins", admins);
		updateTenantInternal(tenantId, tenant);
		producer.publish(getAddAdministratorTopic(), tenant.toJSONString());
	}

	@Override
	public void removeAdministrators(String tenantId, List<String> emails) throws NoSuchUserException{
		JSONObject tenant=getTenantById(tenantId);
		List<String> admins=(List<String>)tenant.get("admins");
		if(admins==null){
			admins= new ArrayList<String>();
		}
		admins.removeAll(emails);
		tenant.put("admins", admins);
		updateTenantInternal(tenantId, tenant);
		producer.publish(getRemoveAdministratorTopic(), tenant.toJSONString());
	}
	@Override
	public void deleteTenantById(String tenantId) throws NoSuchUserException {
		JSONObject tenant=getTenantById(tenantId);
		tenant.put("ownerId", tenant.get("ownerId")+"_DELETION-TAG:"+UUID.randomUUID().toString());
		tenant.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/tenant/"+tenantId, JSONHelper.marshall(tenant));
		producer.publish(getDeleteTenantTopic(), tenant.toJSONString());
	}
	@Override
	public List<JSONObject> findTenantByOwnerId() throws NoSuchUserException{
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> tenants=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/tenant/search/findByOwnerId?ownerId="+customerId,HttpMethod.GET,entity, JSONObject.class);
		if(tenants==null || tenants.getBody()==null){
			throw new NoSuchUserException("No user with given Id");
		}
		List<JSONObject> unmarshalled=(List<JSONObject> )JSONHelper.unmarshall(tenants.getBody());
		return unmarshalled;
	}

	@Cacheable(value = { "tenant-exists" })
	private boolean existsById(String tenantId){
		JSONObject tenant=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/"+tenantId, JSONObject.class);
		if(tenant==null){
			return false;
		}
		return tenant!=null && tenant.size()>0 && ((List)tenant.get("links")).size()>0;
	}
}
