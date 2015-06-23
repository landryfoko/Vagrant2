package com.libertas.vipaas.services.rating;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

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

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRatingException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.messaging.kafka.KafkaConsumer;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("rating")
@Slf4j
public class RatingServiceImpl implements RatingService {


	@Autowired
	AvgUserRatingQueueConsumer avgUserRatingQueueConsumer;
	@Autowired
	AvgUserRatingComputingConsumer avgUserRatingComputingConsumer;

	//@Autowired
	private ProducerTemplate producer;
	@Autowired
	private MetadataProvider metadataProvider;

	private String databaseServiceName;
	private Long avgUserRatingComputationFrequencyMilis=600000l;
	private String ratingCreationTopic;
	private BlockingQueue<JSONObject > ratingQueue =  new LinkedBlockingQueue< JSONObject >();
	@Autowired
	private RestTemplateProxy restTemplateProxy;


	@PostConstruct
	public void init(){
		avgUserRatingQueueConsumer.setRatingQueue(ratingQueue);
		Thread t= new Thread(avgUserRatingQueueConsumer);
		t.start();
		avgUserRatingComputingConsumer.setRatingQueue(ratingQueue);
		t= new Thread(avgUserRatingComputingConsumer);
		t.start();
	}
	public String getRatingCreationTopic() {
		return ratingCreationTopic;
	}

	public void setRatingCreationTopic(String ratingCreationTopic) {
		this.ratingCreationTopic = ratingCreationTopic;
	}

	public Long getAvgUserRatingComputationFrequencyMilis() {
		return avgUserRatingComputationFrequencyMilis;
	}

	public void setAvgUserRatingComputationFrequencyMilis(
			Long avgUserRatingComputationFrequencyMilis) {
		this.avgUserRatingComputationFrequencyMilis = avgUserRatingComputationFrequencyMilis;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}


	@Override
	public JSONObject createRating( String productId, 	JSONObject metadata) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		boolean exists=existsProductById(productId);
		if(!exists){
			throw new NoSuchProductException("No such product");
		}
		metadata.remove("id");
		JSONObject rating= new JSONObject(metadata);
		rating.put("productId", productId);
		rating.put("customerId", customerId);
		rating.put("tenantId", tenantId);
		rating.put("id", UUID.randomUUID().toString());;
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/rating", JSONHelper.marshall(rating));
		producer.publish(getRatingCreationTopic(), rating.toJSONString());
		log.info("Rating event published. Topic: {}. Payload:{}",getRatingCreationTopic(),rating.toJSONString());
		return metadataProvider.filter(rating);
	}

	private JSONObject getRatingByIdInternal(String ratingId) throws NoSuchRatingException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> rating=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/rating/"+ratingId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(rating.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchRatingException("No Such Rating");
	}
	@Override
	public JSONObject getRatingById(String ratingId) throws NoSuchRatingException {
		JSONObject rating=getRatingByIdInternal(ratingId);
		metadataProvider.filter(rating);
		return rating;
	}
/*
	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{rating.playbackLocationRequestTopic}" }, groupId="#{rating.playbackLocationRequestTopic}")
	public void playbackLocationServer(Object stream, Object consumer, byte [] eventByte) throws NoSuchProductException{

	}*/
	@Override
	public JSONObject findAll(String productId, Integer pageSize,	Integer pageNumber, String sortOrder, String sortField) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> ratings=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/rating/search/findByProductIdAndAndTenantId?productId="+productId+"&tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(ratings.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;

	}

	@Override
	public void updateRating(String ratingId, JSONObject metadata) throws NoSuchRatingException {
		JSONObject oldRating=getRatingByIdInternal(ratingId);
		if(oldRating==null ){
			throw new NoSuchRatingException("No such rating");
		}
		metadata.remove("id");
		metadata.remove("deviceId");
		metadata.remove("tenantId");
		metadata.remove("customerId");
		oldRating.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/rating/"+oldRating.get("id"), JSONHelper.marshall(oldRating));
	}

	@Override
	public void deleteRatingById( String ratingId) throws NoSuchRatingException {
		JSONObject rating=getRatingByIdInternal(ratingId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");

		rating.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		rating.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/rating/"+rating.get("id"), JSONHelper.marshall(rating));
	}

	@Cacheable(value = { "product-exists" })
	private boolean existsProductById(String productId){
		return metadataProvider.existsProduct(productId);
	}

}
