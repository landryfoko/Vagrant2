package com.libertas.vipaas.common.messaging.kafka;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;





import com.libertas.vipaas.common.messaging.ProducerTemplate;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class KafkaProducerTemplateImpl implements ProducerTemplate{
	@Autowired
	private Producer<String, Object> producer;
	
	@Override
	public void publish(String topic, Object message) {
		KeyedMessage<String, Object> data = new KeyedMessage<String, Object>(topic,  message);
        producer.send(data);
	}
	@Override
	public void publish(String topic, JSONObject message) {
		if(StringUtils.isEmpty((String)message.get("tenantId"))){
			throw new IllegalArgumentException("Property missing in published event: tenantId");
		}
		if(StringUtils.isEmpty((String)message.get("customerId"))){
			throw new IllegalArgumentException("Property missing in published event: customerId");
		}
		KeyedMessage<String, Object> data = new KeyedMessage<String, Object>(topic,  message.toJSONString());
		producer.send(data);
	}
}
