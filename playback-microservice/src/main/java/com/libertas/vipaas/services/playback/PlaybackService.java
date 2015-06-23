package com.libertas.vipaas.services.playback;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoDeviceSpecMappingException;
import com.libertas.vipaas.common.exceptions.NoMediaFoundException;
import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface PlaybackService {

	JSONObject setPlaybackLocation(Long location, String productId,JSONObject metadata) throws NoSuchProductException, MissingFieldException;
	JSONObject getPlaybackLocation(String productId) throws NoSuchProductException, InterruptedException;
	JSONObject getPlaybackURL(String productId, String offerId,String deviceSpec, String deviceId) throws NoSuchOfferException, NoSuchProductException, NoMediaFoundException, NoDeviceSpecMappingException, NoSuchEntitlementException, InterruptedException;
	void completeWatchingTitle(String productId);
	  
}
