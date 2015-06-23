package com.libertas.vipaas.common.messaging.kafka;

import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.libertas.vipaas.common.messaging.ProducerTemplate;

@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("kafka")
public class KafkaProducerConfig {
	private Map<String,String> producerConfig;

	
	public Map<String, String> getProducerConfig() {
		return producerConfig;
	}
	public void setProducerConfig(Map<String, String> producerConfig) {
		this.producerConfig = producerConfig;
	}
	//@Bean
	public Producer<String, Object> getProducer(){
	    Properties props = new Properties();
	    props.putAll(producerConfig);
        ProducerConfig config = new ProducerConfig(props);
        return new Producer<String, Object>(config);
	}
	//@Bean
	public ProducerTemplate getProducerTemplate(){
		return new KafkaProducerTemplateImpl();
	}
}
