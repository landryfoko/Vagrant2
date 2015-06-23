package com.libertas.vipaas.services.entitlement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping(value={ "/v1/admin/entitlement", "/internal/v1/admin/entitlement"})
public class EntitlementAdminController {

	@Autowired
	EntitlementService entitlementService;
	
	

	@RequestMapping( value="/product/{productId}/offer/{offerId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public void  createEntitlement( @PathVariable( "productId" ) String productId,@PathVariable( "offerId" ) String offerId,@RequestBody JSONObject  metadata) throws Exception {
		entitlementService.createEntitlement(productId, offerId,metadata);
	}
	@RequestMapping(value = "/{entitlementId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findById(@PathVariable( "entitlementId" ) String entitlementId) throws Exception {
        return entitlementService.findById(entitlementId);
    }

	@RequestMapping(value = "/findAll/customer/{customerId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@PathVariable(value="customerId") String customerId,@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber, @RequestParam(value="productIds",required=false)  List<String> productIds,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return entitlementService.findAll(customerId,pageSize, pageNumber,productIds,sortField,sortOrder);
    }
		/*
	@RequestMapping(value = "/{entitlementId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateEntitlement(@PathVariable( "entitlementId" ) String entitlementId, @RequestBody JSONObject  metadata) throws Exception {
		entitlementService.updateEntitlement(entitlementId, metadata);
	}
	@RequestMapping(value = "/{entitlementId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public void disableEntitlement(@PathVariable( "entitlementId" ) String entitlementId, @RequestParam(value="disableDateMillis") Long disableDateMillis) throws Exception {
		entitlementService.disableEntitlement(entitlementId, disableDateMillis);
	}

*/
	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String entitlementId,String productId, JSONObject  device) {
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
