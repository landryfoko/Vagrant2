package com.libertas.vipaas.services.device;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateDeviceException;
import com.libertas.vipaas.common.exceptions.NoSuchDeviceException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@SuppressWarnings("unchecked")
@ConfigurationProperties("device")
public class DeviceServiceImpl implements DeviceService{

	@Autowired
	MetadataProvider metadataProvider;

	@Autowired
	RestTemplateProxy restTemplateProxy;


	private String databaseServiceName;
	//@Autowired
	private ProducerTemplate producer;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public JSONObject registerDevice( String deviceId, JSONObject device) throws DuplicateDeviceException {
		boolean exists=existsById(deviceId);
		if(exists){
			throw new DuplicateDeviceException("Device already exists");
		}
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		device.put("deviceId", deviceId);
		device.put("customerId", credentials.get("customerId"));
		device.put("tenantId", credentials.get("tenantId"));
		device.put("id", UUID.randomUUID().toString());
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/device", JSONHelper.marshall(device));
		return metadataProvider.filter(device);
	}

	@Override
	@Cacheable("devices")
	public JSONObject findAll(Integer pageSize, Integer pageNumber, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		ResponseEntity<JSONObject> device=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/device/search/findByCustomerIdAndTenantId?customerId="+customerId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(device.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;
	}

	@Override
	public void updateDevice(String deviceId, JSONObject device) throws NoSuchDeviceException {
		JSONObject oldDevice=getDeviceByIdInternal(deviceId);
		if(oldDevice==null || oldDevice.get("result")==null|| (((List)oldDevice.get("result")).size()==0)){
			throw new NoSuchDeviceException("No such device");
		}
		oldDevice=new JSONObject((Map)((List)oldDevice.get("result")).get(0));
		device.remove("id");
		device.remove("deviceId");
		device.remove("tenantId");
		device.remove("customerId");
		oldDevice.putAll(device);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/device/"+oldDevice.get("id"), JSONHelper.marshall(oldDevice));
	}
	private JSONObject getDeviceByIdInternal(String deviceId) throws NoSuchDeviceException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> device=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/device/search/findByDeviceIdAndCustomerIdAndTenantId?deviceId="+deviceId+"&customerId="+customerId+"&tenantId="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		if(device==null || device.getBody()==null){
			throw new NoSuchDeviceException("No such device");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(device.getBody());
		if(unmarshalled!=null && unmarshalled.size()>0 ){
			return unmarshalled;
		};
		throw new NoSuchDeviceException("No such device");
	}
	@Override
	@Cacheable("device")
	public JSONObject getDeviceById(String deviceId) throws NoSuchDeviceException {
		JSONObject device=getDeviceByIdInternal(deviceId);
		metadataProvider.filter(device);
		return device;
	}
	@Override
	public void deleteDevice(String deviceId) throws NoSuchDeviceException {
		JSONObject device=getDeviceByIdInternal(deviceId);
		device.put("deviceId", deviceId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		device.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/device/"+device.get("id"), JSONHelper.marshall(device));
	}

	
	@Cacheable(value = { "device-exists" })
	private boolean existsById(String deviceId){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		JSONObject device=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/device/search/findByDeviceIdAndCustomerIdAndTenantId?deviceId="+deviceId+"&customerId="+customerId+"&tenantId="+tenantId, JSONObject.class);
		if(device==null){
			return false;
		}
		return device!=null && device.size()>0 && ((List)device.get("links")).size()>0;
	}

}
