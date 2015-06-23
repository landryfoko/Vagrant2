package com.libertas.vipaas.services.subscriptionpackage;


import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoSuchSubscriptionPackageException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/admin/subscriptionPackage"})
public class SubscriptionPackageAdminController {

	@Autowired
	SubscriptionPackageService subscriptionPackageService;

	@RequestMapping(method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public void  createSubscriptionPackage(@RequestBody JSONObject  metadata) throws Exception {
		if(StringUtils.isEmpty((String) metadata.get("name"))){
			throw new NoSuchSubscriptionPackageException("Missing subscription package name");
		}
		subscriptionPackageService.createSubscriptionPackage(metadata);
	}
	@RequestMapping(value = "/{subscriptionPackageId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getSubscriptionPackageById(@PathVariable( "subscriptionPackageId" ) String subscriptionPackageId) throws Exception {
        return subscriptionPackageService.getSubscriptionPackageById(subscriptionPackageId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber) throws Exception {
        return subscriptionPackageService.findAll(pageSize, pageNumber);
    }

	@RequestMapping(value = "/{subscriptionPackageId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateSubscriptionPackage(@PathVariable( "subscriptionPackageId" ) String subscriptionPackageId, @RequestBody JSONObject  metadata) throws Exception {
		subscriptionPackageService.updateSubscriptionPackage(subscriptionPackageId, metadata);
	}

	@RequestMapping( value = "/{subscriptionPackageId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteSubscriptionPackageById(@PathVariable( "subscriptionPackageId" ) String subscriptionPackageId) throws Exception {
		subscriptionPackageService.deleteSubscriptionPackageById(subscriptionPackageId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String subscriptionPackageId,String productId, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error(JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String id, Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
