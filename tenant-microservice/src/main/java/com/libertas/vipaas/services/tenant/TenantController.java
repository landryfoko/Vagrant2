package com.libertas.vipaas.services.tenant;


import java.util.Arrays;

import javax.validation.Valid;



import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoEmailInRequestException;
import com.libertas.vipaas.common.exceptions.NoNewPasswordInRequestException;
import com.libertas.vipaas.common.exceptions.NoOldPasswordInRequestException;
import com.libertas.vipaas.common.exceptions.NoPasswordInRequestException;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;
import com.libertas.vipaas.common.servlet.LoggingFilter;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping(value={"/v1/tenant","internal/v1/tenant"})
@Slf4j
public class TenantController {

	@Autowired TenantService service;
	@RequestMapping( method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public JSONObject  createTenant(@RequestBody JSONObject  tenant) throws Exception {
		log.info("Tenant creation started");
		if(StringUtils.isEmpty((String) tenant.get("email"))){
			throw new NoEmailInRequestException("Missing email in request");
		}
		if(StringUtils.isEmpty((String) tenant.get("password"))){
			throw new NoPasswordInRequestException("Missing password in request");
		}
		log.info("Tenant creation validation completed");
		return service.createTenant( (String)tenant.get("email"),(String) tenant.get("password"), tenant);
    }

	@RequestMapping(method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateTenant(@RequestBody JSONObject tenant) throws Exception {
		service.updateTenant((String)CredentialsThreadLocal.getCredentials().get("tenantId"), tenant);
    }

	@RequestMapping(value = "/admins", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public void removeAdministrators( @RequestBody String[] emails) throws Exception {
		service.removeAdministrators((String)CredentialsThreadLocal.getCredentials().get("tenantId"),Arrays.asList(emails));
	}

	@RequestMapping(value = "/admins", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void addAdministrators(@RequestBody String[] emails) throws Exception {
		service.addAdministrators((String)CredentialsThreadLocal.getCredentials().get("tenantId"),Arrays.asList(emails));
    }

	@RequestMapping( method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
    public  @ResponseBody  JSONObject  getTenantById() throws Exception {
		JSONObject tenant=service.getTenantById((String)CredentialsThreadLocal.getCredentials().get("tenantId"));
		return tenant;
    }

	@RequestMapping( method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteTenantById() throws Exception {
		service.deleteTenantById((String)CredentialsThreadLocal.getCredentials().get("tenantId"));
	}


	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String text,JSONObject  resource) {
	        return new JSONObject();
	    }
	 public JSONObject error(JSONObject  resource) {
		 return new JSONObject();
	 }
}
