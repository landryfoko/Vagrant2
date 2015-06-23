package com.libertas.vipaas.common.configs;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties("zookeeper")
public class CuratorConfig {
	private String connectionString;
	private Integer baseSleepTimeMs;
	private Integer maxRetries;
	
	public String getConnectionString() {
		return connectionString;
	}


	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}


	public Integer getBaseSleepTimeMs() {
		return baseSleepTimeMs;
	}


	public void setBaseSleepTimeMs(Integer baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}


	public Integer getMaxRetries() {
		return maxRetries;
	}


	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}


	//@Bean
	CuratorFramework getCurator(){
		CuratorFramework client= CuratorFrameworkFactory.newClient(getConnectionString(),  new ExponentialBackoffRetry(getBaseSleepTimeMs(), getMaxRetries()));
		client.start();
		return client;
	}
}
