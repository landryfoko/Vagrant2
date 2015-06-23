package com.libertas.vipaas.services.subscription;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/subscription"})
public class SubscriptionController {


	@RequestMapping( method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody  Map<String, Object>  addSubscription(@RequestBody Map<String, Object>  resource) throws Exception {
        return new HashMap<String, Object>();
    }
	@RequestMapping(value = "/{subscriptionId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> getSubscriptionById(@PathVariable( "subscriptionId" ) String subscriptionId) throws Exception {
        return new HashMap<String, Object>();
    }
	@RequestMapping(value = "/customer/{customerId}/{pageNumber}/{pageSize}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> findAll(@PathVariable( "customerId" ) String customerId,@PathVariable( "pageSize" ) String pageSize, @PathVariable( "pageNumber" ) Long pageNumber) throws Exception {
        return new HashMap<String, Object>();
    }

	@RequestMapping(value = "/{subscriptionId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> updateSubscription(@PathVariable( "subscriptionId" ) String subscriptionId, @RequestBody Map<String, Object>  resource) throws Exception {
        return new HashMap<String, Object>();
    }


	@RequestMapping( value = "/{subscriptionId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  @ResponseBody  Map<String, Object>   deleteSubscriptionById(@PathVariable( "subscriptionId" ) String subscriptionId) throws Exception {
		return new HashMap<String, Object>();
	}

}
