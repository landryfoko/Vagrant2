package com.libertas.vipaas.services.workflow;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/workflow"})
public class WorkflowController {


	@RequestMapping( method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody  Map<String, Object>  addWorkflow(@RequestBody Map<String, Object>  resource) throws Exception {
        return new HashMap<String, Object>();
    }
	@RequestMapping(value = "/{workflowId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> getWorkflowById(@PathVariable( "workflowId" ) String workflowId) throws Exception {
        return new HashMap<String, Object>();
    }
	@RequestMapping(value = "/customer/{customerId}/{pageNumber}/{pageSize}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> findAll(@PathVariable( "customerId" ) String customerId,@PathVariable( "pageSize" ) String pageSize, @PathVariable( "pageNumber" ) Long pageNumber) throws Exception {
        return new HashMap<String, Object>();
    }

	@RequestMapping(value = "/{workflowId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  Map<String, Object> updateWorkflow(@PathVariable( "workflowId" ) String workflowId, @RequestBody Map<String, Object>  resource) throws Exception {
        return new HashMap<String, Object>();
    }


	@RequestMapping( value = "/{workflowId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  @ResponseBody  Map<String, Object>   deleteWorkflowById(@PathVariable( "workflowId" ) String workflowId) throws Exception {
		return new HashMap<String, Object>();
	}
	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
}
