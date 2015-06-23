package com.libertas.vipaas.services.customer;

import java.util.*;

import kafka.javaapi.producer.Producer;

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
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.AuthenticationException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.security.SecurityUtils;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;
import com.libertas.vipaas.common.servlet.LoggingFilter;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

@Service
@SuppressWarnings("unchecked")
@ConfigurationProperties("customer")
@Slf4j
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private MetadataProvider metadataProvider;
	private String resetPasswordTopic;
	private String updateCustomerTopic;
	private String createCustomerTopic;
	private String deleteCustomerTopic;
	@Autowired
	RestTemplateProxy restTemplateProxy;
	//@Autowired
	private ProducerTemplate producer;
	private static final Random RANDOM = new SecureRandom();
	public static final int PASSWORD_LENGTH = 8;
	private String databaseServiceName;



	public String getDeleteCustomerTopic() {
		return deleteCustomerTopic;
	}

	public void setDeleteCustomerTopic(String deleteCustomerTopic) {
		this.deleteCustomerTopic = deleteCustomerTopic;
	}

	public String getUpdateCustomerTopic() {
		return updateCustomerTopic;
	}

	public void setUpdateCustomerTopic(String updateCustomerTopic) {
		this.updateCustomerTopic = updateCustomerTopic;
	}

	public String getCreateCustomerTopic() {
		return createCustomerTopic;
	}

	public void setCreateCustomerTopic(String createCustomerTopic) {
		this.createCustomerTopic = createCustomerTopic;
	}

	public String getResetPasswordTopic() {
		return resetPasswordTopic;
	}

	public void setResetPasswordTopic(String resetPasswordTopic) {
		this.resetPasswordTopic = resetPasswordTopic;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}
	@Override
	public JSONObject createCustomer(String email, String password,JSONObject customer) throws DuplicateUserException {
		boolean exists=existsByEmail(email);
		if(exists){
			throw new DuplicateUserException("User already exists with given email");
		}
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		customer.put("id", UUID.randomUUID().toString());
		customer.put("email", email);
		customer.put("tenantId", tenantId);
		customer.put("password", SecurityUtils.hash((String)customer.get("password")));
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/customer", JSONHelper.marshall(customer));
		producer.publish(getCreateCustomerTopic(), customer.toJSONString());
		return metadataProvider.filter(customer);
	}

	@Override
	@CacheEvict("customers")
	public void updateCustomer(String customerId, JSONObject customer) throws NoSuchUserException {
		JSONObject oldCustomer=getInternalCustomerById(customerId);
		customer.remove("id");
		customer.remove("tenantId");
		customer.remove("password");
		oldCustomer.putAll(customer);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/customer/"+customerId, JSONHelper.marshall(oldCustomer));
		producer.publish(getUpdateCustomerTopic(), customer.toJSONString());
	}
	private JSONObject getInternalCustomerById(String customerId) throws NoSuchUserException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> customer=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/customer/"+customerId,HttpMethod.GET,entity, JSONObject.class);
		if(customer==null || customer.getBody()==null){
			throw new NoSuchUserException("No user with given Id");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(customer.getBody());

		if(unmarshalled!=null && unmarshalled.size()>0 ){
			return unmarshalled;
		};
		throw new NoSuchUserException("No user with given Id");
	}

	@Override
	@Cacheable("customers")
	public JSONObject getCustomerById(String customerId) throws NoSuchUserException {
		JSONObject unmarshalled=getInternalCustomerById(customerId);
		if(unmarshalled!=null && unmarshalled.size()>0 ){
			metadataProvider.filter(unmarshalled);
			return unmarshalled;
		};
		throw new NoSuchUserException("No user with given Id");
	}

	@Override
	public JSONObject validateCredentials(String email, String password) throws AuthenticationException{
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<JSONObject> customer=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/customer/search/findByEmailAndTenantId?email="+email+"&tenantId="+tenantId+"&size=1&page=0",HttpMethod.GET,entity, JSONObject.class);
		if(customer==null || customer.getBody()==null){
			throw new AuthenticationException("Could not authenticate user");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(customer.getBody());
		if(unmarshalled==null || unmarshalled.size()==0 || unmarshalled.get("result")==null  ){
			throw new AuthenticationException("Could not authenticate user");
		};
		JSONObject user=new JSONObject((Map)((List)unmarshalled.get("result")).get(0));
		SecurityUtils.validatePassword(password,user.get("password").toString());
		metadataProvider.filter(user);
		return user;
	}
	@Override
	public JSONObject login(String email, String password) throws AuthenticationException{
		JSONObject user=validateCredentials(email, password);
		metadataProvider.filter(user);
		return user;
	}



	@Override
	public void changePassword(String email, String oldPassword,String newPassword) throws AuthenticationException {
		JSONObject user=validateCredentials(email, oldPassword);
		user.put("password", SecurityUtils.hash(newPassword));
		user.remove("id");
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/customer/"+user.get("id"), user);
	}

	public static String generateRandomPassword(){
	      String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";
	      String pw = "";
	      for (int i=0; i<PASSWORD_LENGTH; i++)
	      {
	          int index = (int)(RANDOM.nextDouble()*letters.length());
	          pw += letters.substring(index, index+1);
	      }
	      return pw;
	 }

	@Override
	public void resetPassword(String email) throws NoSuchUserException {
		String password=generateRandomPassword();
		JSONObject user=getCustomerByEmail(email);
		user.remove("id");
		user.put("password", SecurityUtils.hash(password));
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/customer/"+user.get("id"), user);
		JSONObject message=new JSONObject();
		message.put("email", email);
		message.put("password", password);
		producer.publish(getResetPasswordTopic(), message.toJSONString());
	}

	@Override
	public void logout(String customerId, JSONObject  metadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteCustomerById(String customerId) throws NoSuchUserException {
		JSONObject customer=getInternalCustomerById(customerId);
		customer.put("tenantId", customer.get("tenantId")+"_DELETION-TAG:"+UUID.randomUUID().toString());
		customer.put("email", customer.get("email")+"_DELETION-TAG:"+UUID.randomUUID().toString());
		customer.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/customer/"+customerId, JSONHelper.marshall(customer));
		producer.publish(getDeleteCustomerTopic(), customer.toJSONString());
	}

	private boolean existsByEmail(String email){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject customer=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/customer/search/findByEmailAndTenantId?email="+email+"&tenantId="+tenantId+"&size=1"+"&page=1", JSONObject.class);
		if(customer==null ){
			return false;
		}
		return customer!=null && customer.size()>0 && ((List)customer.get("links")).size()>0;
	}
	@Cacheable(value = { "customer-exists" })
	private boolean existsById(String customerId){
		JSONObject customer=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/"+customerId, JSONObject.class);
		if(customer==null){
			return false;
		}
		return customer!=null && customer.size()>0 && ((List)customer.get("links")).size()>0;
	}

	@Override
	public JSONObject getCustomerByEmail(String email)throws NoSuchUserException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject customer=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/customer/search/findByEmailAndTenantId?email="+email+"&tenantId="+tenantId+"&size=1"+"&page=0", JSONObject.class);
		if(customer==null ){
			throw new NoSuchUserException("no such user");
		}
		JSONObject unmarshalled=JSONHelper.marshall(customer);
		metadataProvider.filter(unmarshalled);
		return unmarshalled;
	}
}
