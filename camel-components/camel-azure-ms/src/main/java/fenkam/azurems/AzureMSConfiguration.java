package fenkam.azurems;

import com.microsoft.windowsazure.services.media.MediaContract;

public class AzureMSConfiguration {
	private String mediaServiceURI;
	private String OAuthURI;
	private String clientId;
	private String scope;
	private String clientSecret;
	private Integer encodePriority;
	private Double policyDurationInMinute;
	
	
	public String getOAuthURI() {
		return OAuthURI;
	}

	public void setOAuthURI(String oAuthURI) {
		OAuthURI = oAuthURI;
	}

	public Double getPolicyDurationInMinute() {
		return policyDurationInMinute;
	}

	public void setPolicyDurationInMinute(Double policyDurationInMinute) {
		this.policyDurationInMinute = policyDurationInMinute;
	}

	public Integer getEncodePriority() {
		return encodePriority;
	}

	public void setEncodePriority(Integer encodePriority) {
		this.encodePriority = encodePriority;
	}

	private MediaContract mediaService;

	public MediaContract getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaContract mediaService) {
		this.mediaService = mediaService;
	}

	public String getMediaServiceURI() {
		return mediaServiceURI;
	}

	public void setMediaServiceURI(String mediaServiceURI) {
		this.mediaServiceURI = mediaServiceURI;
	}


	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	
	
	

}
