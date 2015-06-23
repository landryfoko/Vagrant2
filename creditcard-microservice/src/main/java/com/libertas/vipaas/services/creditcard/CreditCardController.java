package com.libertas.vipaas.services.creditcard;

import java.util.HashMap;
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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/creditcard"})
public class CreditCardController {

	@Autowired
	CreditCardService creditcardService;

	@RequestMapping(method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  addCreditCard(@RequestBody JSONObject  metadata) throws Exception {
		return creditcardService.addCreditCard( metadata);
	}
	@RequestMapping(value = "/{creditcardId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getCreditCardById(@PathVariable( "creditcardId" ) String creditcardId) throws Exception {
        return creditcardService.getCreditCardById(creditcardId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return creditcardService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{creditcardId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateCreditCard(@PathVariable( "creditcardId" ) String creditcardId, @RequestBody JSONObject  metadata) throws Exception {
		creditcardService.updateCreditCard(creditcardId, metadata);
	}

	@RequestMapping( value = "/{creditcardId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteCreditCardById(@PathVariable( "creditcardId" ) String creditcardId) throws Exception {
		creditcardService.deleteCreditCardById(creditcardId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String creditcardId,String productId, JSONObject  device) {
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
