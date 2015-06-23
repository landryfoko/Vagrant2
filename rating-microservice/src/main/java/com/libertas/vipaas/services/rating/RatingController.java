package com.libertas.vipaas.services.rating;

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
@RequestMapping({ "/v1/rating"})
public class RatingController {

	@Autowired
	RatingService ratingService;

	@RequestMapping( value="/product/{productId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createRating( @PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		return ratingService.createRating(productId, metadata);
	}
	@RequestMapping(value = "/{ratingId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getRatingById(@PathVariable( "ratingId" ) String ratingId) throws Exception {
        return ratingService.getRatingById(ratingId);
    }

	@RequestMapping(value = "/findAll/product/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@PathVariable( "productId" ) String productId,@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return ratingService.findAll(productId,pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{ratingId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateRating(@PathVariable( "ratingId" ) String ratingId, @RequestBody JSONObject  metadata) throws Exception {
		ratingService.updateRating(ratingId, metadata);
	}

	@RequestMapping( value = "/{ratingId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteRatingById(@PathVariable( "ratingId" ) String ratingId) throws Exception {
		ratingService.deleteRatingById(ratingId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	 }
	 public JSONObject error(String ratingId,String productId, JSONObject  device) {
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
