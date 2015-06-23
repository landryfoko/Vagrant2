package com.libertas.vipaas.services.recommendation;

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
@RequestMapping({ "/v1/recommendation"})
public class RecommendationController {

	@Autowired
	RecommendationService recommendationService;

	@RequestMapping(value = "/product/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getRecommendationByProductId(@PathVariable( "productId" ) String productId) throws Exception {
        return recommendationService.getRecommendationByProductId(productId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return recommendationService.findAll(pageSize, pageNumber,sortField,sortOrder);
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
