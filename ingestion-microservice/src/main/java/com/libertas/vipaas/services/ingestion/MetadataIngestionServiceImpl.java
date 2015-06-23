package com.libertas.vipaas.services.ingestion;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.messaging.kafka.KafkaConsumer;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CorsFilter;

@Service
@Slf4j
public class MetadataIngestionServiceImpl  extends RouteBuilder  {

/*	@Autowired
	CamelContext camelContext;

	@PostConstruct
	public void start() throws Exception{
		camelContext.addRoutes(this);
		camelContext.start();
	}
	@Autowired
	MetadataProvider metadataProvider;
	@Autowired
	RestTemplate databaseRestTemplate;
	@Autowired
	private ProducerTemplate producer;
	private String databaseServiceName;
	*/
	Processor myProcessor = new Processor() {
	    public void process(Exchange exchange) {
	    	log.info("Called with exchange: " + exchange);
	    }
	};

	@Override
	public void configure() throws Exception {

		from("timer:test?fixedRate=true&period=10s&repeatCount=1000")
         .log("Running metadata ingestion example route")
         .process(myProcessor)
         .autoStartup(true);
	}


}
