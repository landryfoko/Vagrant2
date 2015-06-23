package com.libertas.vipaas.services.ingestion;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.camel.impl.DefaultHeaderFilterStrategy;
import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.libertas.vipass.ingestion.loader.StaticModuleLoader;
import com.libertas.vipass.ingestion.wacthers.DirectoryChangeWatcher;

@Slf4j
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("ingestion")
@ImportResource({"classpath:routes/ingest-edgecast.xml","classpath:routes/ingest-uplynk.xml","classpath:routes/upload-to-s3.xml","classpath:routes/ingest-metadata.xml","classpath:routes/ingest-preview.xml","classpath:routes/ingest-image.xml","classpath:routes/ingest-video.xml"})
//@ImportResource({"applicationContext.xml"})
public class IngestionConfig {
	@Getter
	private final Map<String, String> metadataConfig = new HashMap<String, String>();
	@Getter
	private final Map<String, String> previewConfig = new HashMap<String, String>();
	@Getter
	private final Map<String, String> videoConfig = new HashMap<String, String>();
	@Getter
	private final Map<String, String> devicePresetMapping = new HashMap<String, String>();
	@Getter @Setter
	private String amazonAccessKey;
	@Getter @Setter
	private String amazonSecretKey;
	@Getter @Setter
	private int amazonMaxConnectionCount;
	
	@Bean(name="libertasHeaderFilterStrategy")
	public DefaultHeaderFilterStrategy getHeaderFilterStrategy(final Environment env){
		DefaultHeaderFilterStrategy filter= new DefaultHeaderFilterStrategy();
		filter.setOutFilterPattern(".*Libertas.*");
		return filter;
	}
	
	@Bean(name="metadataConfig")
	Properties getMetadataConfig(final Environment env){
		final Properties p= new Properties();
		p.putAll(getMetadataConfig());
		return p;
	}
	@Bean(name="previewConfig")
	Properties getPreviewConfig(final Environment env){
		final Properties p= new Properties();
		p.putAll(getMetadataConfig());
		p.putAll(getPreviewConfig());
		JSONObject json= new JSONObject();
		json.putAll(getDevicePresetMapping());
		p.put("devicePresetMapping", json.toJSONString());
		log.info("returing preview config{}",p);
		return p;
	}
	@Bean(name="videoConfig")
	Properties getVideoConfig(final Environment env){
		final Properties p= new Properties();
		p.putAll(getPreviewConfig(env));
		p.putAll(getVideoConfig());
		return p;
	}
	
	
	@Bean
	public BasicAWSCredentials getAWSCredentials(final Environment env){
        //return new BasicAWSCredentials("AKIAIM5OO2IPEXXAIDXA", "BlI6xlDjgIjjnFhzC0soSiPGSXMz0wlU5Io570jk");
		return new BasicAWSCredentials(getAmazonAccessKey(), getAmazonSecretKey());
	}
	@Bean
	public ClientConfiguration getClientConfiguration(final Environment env){
		ClientConfiguration config= new ClientConfiguration();
		config.setMaxConnections(getAmazonMaxConnectionCount());
		return config;
	}
	@Bean(name="sdb")
	public AmazonSimpleDBClient getAmazonSimpleDBClient(final Environment env){
		return new AmazonSimpleDBClient(getAWSCredentials(env), getClientConfiguration(env));
	}
	@Bean(name="s3")
	public AmazonS3Client getAmazonS3Client(final Environment env){
		return new AmazonS3Client(getAWSCredentials(env), getClientConfiguration(env));
	}
}
