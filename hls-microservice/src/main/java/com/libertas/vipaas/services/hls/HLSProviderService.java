package com.libertas.vipaas.services.hls;

import org.json.simple.JSONObject;


public interface HLSProviderService {

	JSONObject getHLSCredentials(final JSONObject hlsRequest);
}
