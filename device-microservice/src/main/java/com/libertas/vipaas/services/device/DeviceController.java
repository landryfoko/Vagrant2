package com.libertas.vipaas.services.device;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/v1/device")
public class DeviceController {

	@Autowired
	DeviceService deviceService;

	@RequestMapping(value = "/{deviceId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  registerDevice(@PathVariable( "deviceId" ) String deviceId, @RequestBody JSONObject  device) throws Exception {
		return deviceService.registerDevice(deviceId, device);
	}
	@RequestMapping(value = "/{deviceId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getDeviceById(@PathVariable( "deviceId" ) String deviceId) throws Exception {
       return deviceService.getDeviceById(deviceId);
    }
	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
		return deviceService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{deviceId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void   updateDevice(@PathVariable( "deviceId" ) String deviceId, @RequestBody JSONObject  device) throws Exception {
		deviceService.updateDevice(deviceId, device);
	}


	@RequestMapping( value = "/{deviceId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  @ResponseBody  JSONObject   deleteDeviceById(@PathVariable( "deviceId" ) String deviceId) throws Exception {
		return new JSONObject();
	}
	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error(String text,String text2,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error(JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String id, Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }
}
