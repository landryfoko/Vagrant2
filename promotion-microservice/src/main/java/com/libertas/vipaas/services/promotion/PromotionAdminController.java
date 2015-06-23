package com.libertas.vipaas.services.promotion;

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
@RequestMapping({ "/v1/admin/promotion"})
public class PromotionAdminController {

	@Autowired
	PromotionService promotionService;

	@RequestMapping( value="/{name}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createPromotion( @PathVariable( "name" ) String name,@RequestBody JSONObject  metadata) throws Exception {
		return promotionService.createPromotion(name, metadata);
	}


	@RequestMapping(value = "/{promotionId}/untag", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void untagPromotion(@PathVariable( "promotionId" ) String promotionId, @RequestBody String []  tags) throws Exception {
		promotionService.untagPromotion(promotionId, Arrays.asList(tags));
	}



	@RequestMapping(value = "/{promotionId}/tag", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void tagPromotion(@PathVariable( "promotionId" ) String promotionId, @RequestBody String []  tags) throws Exception {
		promotionService.tagPromotion(promotionId, Arrays.asList(tags));
	}

	@RequestMapping(value = "/{promotionId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updatePromotion(@PathVariable( "promotionId" ) String promotionId, @RequestBody JSONObject  metadata) throws Exception {
		promotionService.updatePromotion(promotionId, metadata);
	}

	@RequestMapping( value = "/{promotionId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deletePromotionById(@PathVariable( "promotionId" ) String promotionId) throws Exception {
		promotionService.deletePromotionById(promotionId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String promotionId,String productId, JSONObject  device) {
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
