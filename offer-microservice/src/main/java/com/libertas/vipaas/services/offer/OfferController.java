package com.libertas.vipaas.services.offer;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/offer"})
public class OfferController {

	@Autowired
	OfferService offerService;

	@RequestMapping(value = "/{offerId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getOfferById(@PathVariable( "offerId" ) String offerId) throws Exception {
        return offerService.findById(offerId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return offerService.findAll(pageSize, pageNumber, sortField, sortOrder);
    }


	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error( Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
