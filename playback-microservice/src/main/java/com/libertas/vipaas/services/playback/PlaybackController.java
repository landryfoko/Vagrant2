package com.libertas.vipaas.services.playback;

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
@RequestMapping({ "/v1/playback","/internal/v1/playback"})
public class PlaybackController {

	@Autowired
	PlaybackService playbackService;

	@RequestMapping( value="/location/{location}/product/{productId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  setPlaybackLocation(@PathVariable( "location" ) Long location,@PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		return playbackService.setPlaybackLocation(location,productId, metadata);
	}

	@RequestMapping( value="/location/product/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public  @ResponseBody  JSONObject  getPlaybackLocation(@PathVariable( "productId" ) String productId) throws Exception {
		return playbackService.getPlaybackLocation(productId);
	}

	@RequestMapping(value = "/url", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getPlaybackURL(@RequestParam( "productId" ) String productId,@RequestParam( "offerId" ) String offerId,@RequestParam( "deviceSpec" ) String deviceSpec,@RequestParam( "deviceId" ) String deviceId) throws Exception {
        return playbackService.getPlaybackURL(productId,offerId,deviceSpec,deviceId);
    }
	@RequestMapping(value = "/complete", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public void  completeWatchingTitle(@RequestParam( "productId" ) String productId) throws Exception {
		 playbackService.completeWatchingTitle(productId);
	}


	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String playbackId,String productId, JSONObject  device) {
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
