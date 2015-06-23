package com.libertas.vipaas.services.purchase;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/purchase"})
public class PurchaseController {

	@Autowired
	PurchaseService purchaseService;

	@RequestMapping( method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject purchase(@RequestBody JSONObject request) throws Exception {
		return purchaseService.purchase(request);
	}

	@RequestMapping(value = "/{purchaseId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getPurchaseById(@PathVariable( "purchaseId" ) String purchaseId) throws Exception {
        return purchaseService.getPurchaseById(purchaseId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return purchaseService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }


	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String text, Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
