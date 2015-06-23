package com.libertas.vipaas.services.recommendation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("rotten-tomatoess")
public class RottenTomatoesConfiguration {

	private String applicationName;
	private String applicationKey;
	private String searchQueryUrlTemplate;
	private String similarQueryUrlTemplate;

	public String getApplicationKey() {
		return applicationKey;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public String getSearchQueryUrlTemplate() {
		return searchQueryUrlTemplate;
	}
	public String getSimilarQueryUrlTemplate() {
		return similarQueryUrlTemplate;
	}
	public void setApplicationKey(final String applicationKey) {
		this.applicationKey = applicationKey;
	}
	public void setApplicationName(final String applicationName) {
		this.applicationName = applicationName;
	}
	public void setSearchQueryUrlTemplate(final String searchQueryUrlTemplate) {
		this.searchQueryUrlTemplate = searchQueryUrlTemplate;
	}
	public void setSimilarQueryUrlTemplate(final String similarQueryUrlTemplate) {
		this.similarQueryUrlTemplate = similarQueryUrlTemplate;
	}

}
