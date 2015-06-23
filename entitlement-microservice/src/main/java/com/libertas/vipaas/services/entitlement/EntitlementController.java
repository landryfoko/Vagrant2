package com.libertas.vipaas.services.entitlement;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping(value={"/v1/entitlement/check","/internal/v1/entitlement/check"})
public class EntitlementController {

	@Autowired
	EntitlementService entitlementService;

	@RequestMapping( value="/product/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody  JSONObject findValidOne( @PathVariable( "productId" ) String productId) throws NoSuchEntitlementException, NoSuchProductException  {
		return entitlementService.findValidOne(productId);
	}


	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }

}
