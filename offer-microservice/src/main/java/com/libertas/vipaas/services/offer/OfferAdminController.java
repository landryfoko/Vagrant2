package com.libertas.vipaas.services.offer;

import kafka.javaapi.producer.Producer;

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
@RequestMapping({ "/v1/admin/offer"})
public class OfferAdminController {

	@Autowired
	OfferService offerService;
	
//	@Autowired
//	private Producer<String,Object> producer;
	
	@RequestMapping( value="/{name}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createOffer( @PathVariable( "name" ) String name,@RequestBody JSONObject  metadata) throws Exception {
		return offerService.createOffer(name, metadata);
	}
	@RequestMapping(value = "/{offerId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getOfferById(@PathVariable( "offerId" ) String offerId) throws Exception {
        return offerService.findById(offerId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return offerService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{offerId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateOffer(@PathVariable( "offerId" ) String offerId, @RequestBody JSONObject  metadata) throws Exception {
		offerService.updateOffer(offerId, metadata);
	}

	@RequestMapping( value = "/{offerId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteOfferById(@PathVariable( "offerId" ) String offerId) throws Exception {
		offerService.deleteOfferById(offerId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String offerId,String productId, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error( Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
