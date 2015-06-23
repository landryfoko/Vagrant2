package com.libertas.vipaas.services.subscriptionpackage;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/subscriptionpackage"})
public class SubscriptionPackageController {

	@Autowired
	SubscriptionPackageService subscriptionPackageService;

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber) throws Exception {
        return subscriptionPackageService.findAll(pageSize, pageNumber);
    }


	 public JSONObject error(String productId, JSONObject  device) {
		 return new JSONObject();
	 }

}
