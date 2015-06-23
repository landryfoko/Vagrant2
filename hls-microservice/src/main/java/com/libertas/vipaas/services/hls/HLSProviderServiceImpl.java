package com.libertas.vipaas.services.hls;

import org.json.simple.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
@ConfigurationProperties("hls")
public class HLSProviderServiceImpl implements HLSProviderService{
	private String hlsKey;
	private String rottenUrl;


	public String getRottenUrl() {
		return rottenUrl;
	}

	public void setRottenUrl(String rottenUrl) {
		this.rottenUrl = rottenUrl;
	}

	@Override
    public JSONObject getHLSCredentials(final JSONObject hlsRequest)  {
		final JSONObject o=new JSONObject();
		o.put("key", getHlsKey());
		return o;
	}

    public String getHlsKey() {
        return hlsKey;
    }

	public void setHlsKey(final String hlsKey) {
		this.hlsKey = hlsKey;
	}

}
