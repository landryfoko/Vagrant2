package com.libertas.vipaas.services.rating;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.libertas.vipaas.common.messaging.kafka.KafkaConsumerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.*;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.extern.slf4j.Slf4j;

@Component
@Configuration
@ConfigurationProperties("rating")
@Slf4j
public class AvgUserRatingQueueConsumer implements Runnable{


	private Integer maxNumberOfAvgUserRatingComputationConsumer;
	private String avgUserRatingKafkaGroupId;
	private String ratingCreationTopic;
	private BlockingQueue<JSONObject > ratingQueue;
	@Autowired
	private KafkaConsumerConfig consumerConfig;


	public BlockingQueue<JSONObject> getRatingQueue() {
		return ratingQueue;
	}
	public void setRatingQueue(BlockingQueue<JSONObject> ratingQueue) {
		this.ratingQueue = ratingQueue;
	}
	public Integer getMaxNumberOfAvgUserRatingComputationConsumer() {
		return maxNumberOfAvgUserRatingComputationConsumer;
	}
	public void setMaxNumberOfAvgUserRatingComputationConsumer(
			Integer maxNumberOfAvgUserRatingComputationConsumer) {
		this.maxNumberOfAvgUserRatingComputationConsumer = maxNumberOfAvgUserRatingComputationConsumer;
	}

	public String getAvgUserRatingKafkaGroupId() {
		return avgUserRatingKafkaGroupId;
	}
	public void setAvgUserRatingKafkaGroupId(
			String avgUserRatingKafkaGroupId) {
		this.avgUserRatingKafkaGroupId = avgUserRatingKafkaGroupId;
	}
	public String getRatingCreationTopic() {
		return ratingCreationTopic;
	}
	public void setRatingCreationTopic(String ratingCreationTopic) {
		this.ratingCreationTopic = ratingCreationTopic;
	}
	public KafkaConsumerConfig getConsumerConfig() {
		return consumerConfig;
	}
	public void setConsumerConfig(KafkaConsumerConfig consumerConfig) {
		this.consumerConfig = consumerConfig;
	}
	@Override
	public void run() {
		log.info("Kafka rating group Id {}",getAvgUserRatingKafkaGroupId());
		log.info("getMaxNumberOfAvgUserRatingComputationConsumer {}",getMaxNumberOfAvgUserRatingComputationConsumer());
		String groupId=getAvgUserRatingKafkaGroupId();
		log.info("Group Id {} computed for {}",groupId,this.getClass().getName());

		Properties props= new Properties();
		props.putAll(getConsumerConfig().getConsumerConfig());
		props.put("group.id", groupId);
		log.info("Initiating kafka consumer with properties: {}",props);
		ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));


		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(getRatingCreationTopic(), new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(getRatingCreationTopic()).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()){
			String event=new String(it.next().message());
			log.info("Rating event received. Topic:{}, Payload:{}",getRatingCreationTopic(),event);
			JSONObject json=(JSONObject)JSONValue.parse(event);
			ratingQueue.add(json);
		}
	}
}
