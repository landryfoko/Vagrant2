package com.libertas.vipaas.common.messaging.kafka;

import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
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
public class KafkaConsumerConfig {
	private Map<String,String> consumerConfig;

	public Map<String, String> getConsumerConfig() {
		return consumerConfig;
	}

	public void setConsumerConfig(Map<String, String> consumerConfig) {
		this.consumerConfig = consumerConfig;
	}
	@Bean(name={"defaultKafkaConfig"})
	public  Map<String, String> getKafkaConsumerConfig(){
	
		return consumerConfig;
	}
	
}
