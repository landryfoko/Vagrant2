package com.libertas.vipaas.common.messaging;

import org.json.simple.JSONObject;

public interface ProducerTemplate {
	void publish(String topic, Object message);

	void publish(String topic, JSONObject message);
}
