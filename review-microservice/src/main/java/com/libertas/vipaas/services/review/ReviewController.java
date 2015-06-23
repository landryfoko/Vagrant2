package com.libertas.vipaas.services.review;

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
@RequestMapping({ "/v1/review"})
public class ReviewController {

	@Autowired
	ReviewService reviewService;

	@RequestMapping( value="/product/{productId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createReview( @PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		return reviewService.createReview(productId, metadata);
	}
	@RequestMapping(value = "/{reviewId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getReviewById(@PathVariable( "reviewId" ) String reviewId) throws Exception {
        return reviewService.getReviewById(reviewId);
    }

	@RequestMapping(value = "/findAll/product/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@PathVariable( "productId" ) String productId,@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return reviewService.findAll(productId,pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{reviewId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateReview(@PathVariable( "reviewId" ) String reviewId, @RequestBody JSONObject  metadata) throws Exception {
		reviewService.updateReview(reviewId, metadata);
	}

	@RequestMapping( value = "/{reviewId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteReviewById(@PathVariable( "reviewId" ) String reviewId) throws Exception {
		reviewService.deleteReviewById(reviewId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String reviewId,String productId, JSONObject  device) {
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
