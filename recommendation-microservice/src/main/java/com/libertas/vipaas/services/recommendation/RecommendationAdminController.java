package com.libertas.vipaas.services.recommendation;

import java.util.Arrays;
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
@RequestMapping({ "/v1/admin/recommendation"})
public class RecommendationAdminController {

	@Autowired
	RecommendationService recommendationService;

	@RequestMapping( value="/product/{productId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public JSONObject  createRecommendation( @PathVariable( "productId" ) String name,@RequestBody JSONObject [] recommendationList) throws Exception {
		return recommendationService.createRecommendation(name, Arrays.asList(recommendationList));
	}



	@RequestMapping(value = "/{recommendationId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateRecommendation(@PathVariable( "recommendationId" ) String recommendationId, @RequestBody JSONObject  metadata) throws Exception {
		recommendationService.updateRecommendation(recommendationId, metadata);
	}

	@RequestMapping( value = "/product/{productId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteRecommendationByProductId(@PathVariable( "productId" ) String productId) throws Exception {
		recommendationService.deleteRecommendationByProductId(productId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String recommendationId,String productId, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error(JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
