package com.libertas.vipaas.services.hls;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.json.JSONHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/v1/hls")
public class HLSProviderController {

	@Autowired
	HLSProviderService hlsProviderService;

	public JSONObject error(final JSONObject  device) {
	        return new JSONObject();
	    }

	
    @RequestMapping(method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody  JSONObject getHLSCredentials(@RequestBody final JSONObject  hlsRequest) throws Exception {
		return hlsProviderService.getHLSCredentials(hlsRequest);
	}
}
