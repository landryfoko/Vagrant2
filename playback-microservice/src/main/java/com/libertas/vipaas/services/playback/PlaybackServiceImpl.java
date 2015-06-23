package com.libertas.vipaas.services.playback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoDeviceSpecMappingException;
import com.libertas.vipaas.common.exceptions.NoMediaFoundException;
import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.messaging.kafka.KafkaConsumer;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("playback")
@Slf4j
public class PlaybackServiceImpl implements PlaybackService {


	@Autowired
	MetadataProvider metadataProvider;
	@Autowired
	private RestTemplateProxy databaseRestTemplateProxy;

	//@Autowired
	private ProducerTemplate producer;
	private String databaseServiceName;
	private String entitlementServiceUrlTemplate;
	private String solrReadUrl;
	private Map<String,String> deviceSpecMapping;
	private Long playbackResponseTimoutMillis;
	private Integer playbackResponseMinCount;
	private String playbackLocationResponseTopic;
	private String playbackLocationRequestTopic;
	private Integer playbackLocationFrequencyMillis;

	private final Map<String, Pair<Semaphore,List<JSONObject>>> playbackResponses= new HashMap<String, Pair<Semaphore,List<JSONObject>>>();

	private Map<String,Map<String,JSONObject>> locations= new HashMap<String,Map<String,JSONObject>>();

	public Integer getPlaybackLocationFrequencyMillis() {
		return playbackLocationFrequencyMillis;
	}

	public void setPlaybackLocationFrequencyMillis(
			Integer playbackLocationFrequencyMillis) {
		this.playbackLocationFrequencyMillis = playbackLocationFrequencyMillis;
	}

	public String getPlaybackLocationResponseTopic() {
		return playbackLocationResponseTopic;
	}

	public void setPlaybackLocationResponseTopic(
			String playbackLocationResponseTopic) {
		this.playbackLocationResponseTopic = playbackLocationResponseTopic;
	}

	public String getPlaybackLocationRequestTopic() {
		return playbackLocationRequestTopic;
	}

	public void setPlaybackLocationRequestTopic(String playbackLocationRequestTopic) {
		this.playbackLocationRequestTopic = playbackLocationRequestTopic;
	}

	public Integer getPlaybackResponseMinCount() {
		return playbackResponseMinCount;
	}

	public void setPlaybackResponseMinCount(Integer playbackResponseMinCount) {
		this.playbackResponseMinCount = playbackResponseMinCount;
	}

	public Long getPlaybackResponseTimoutMillis() {
		return playbackResponseTimoutMillis;
	}

	public void setPlaybackResponseTimoutMillis(Long playbackResponseTimoutMillis) {
		this.playbackResponseTimoutMillis = playbackResponseTimoutMillis;
	}

	public String getEntitlementServiceUrlTemplate() {
		return entitlementServiceUrlTemplate;
	}

	public void setEntitlementServiceUrlTemplate(String entitlementServiceUrlTemplate) {
		this.entitlementServiceUrlTemplate = entitlementServiceUrlTemplate;
	}

	public Map<String, String> getDeviceSpecMapping() {
		return deviceSpecMapping;
	}

	public void setDeviceSpecMapping(Map<String, String> deviceSpecMapping) {
		this.deviceSpecMapping = deviceSpecMapping;
	}

	public String getSolrReadUrl() {
		return solrReadUrl;
	}

	public void setSolrReadUrl(String solrReadUrl) {
		this.solrReadUrl = solrReadUrl;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@Override
	public JSONObject setPlaybackLocation(Long location, String productId,JSONObject metadata)throws NoSuchProductException, MissingFieldException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		boolean exists=metadataProvider.existsProduct(productId);
		if(!exists){
			throw new NoSuchProductException("No such product");
		}
		String deviceId=(String)metadata.get("deviceId");
		if(StringUtils.isEmpty(deviceId)){
			throw new MissingFieldException("Missing field deviceId in request");
		}
		metadata.putAll(JSONHelper.make(new String[]{"date", "tenantId","location","productId","customerId"}, new Object[]{System.currentTimeMillis(),tenantId,location,productId,customerId}));
		locations.put(customerId, locations.get(customerId)==null?new HashMap<String,JSONObject>():locations.get(customerId));
		locations.get(customerId).put(deviceId,metadata);
		JSONObject response= JSONHelper.make("frequency",getPlaybackLocationFrequencyMillis());
		return response;
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{playback.playbackLocationRequestTopic}" }, groupId="playback-server-#random")
	public void playbackLocationServer(Object stream, Object consumer, byte [] eventByte) throws NoSuchProductException{
		String eventString= new String(eventByte);
		JSONObject eventJson= (JSONObject)JSONValue.parse(eventString);
		String customerId=(String)eventJson.get("customerId");
		String tenantId=(String)eventJson.get("tenantId");
		String productId=(String)eventJson.get("productId");
		Map<String,JSONObject> devices=locations.get(customerId);
		JSONObject resp= JSONHelper.make(new String[]{"customerId", "productId","tenantId","date"}, new Object[]{customerId,productId,tenantId,System.currentTimeMillis()});
		long date=-1;
		if(devices!=null){
			for(JSONObject device:devices.values()){
				if((Long)device.get("date")>date && device.get("productId").toString().equalsIgnoreCase(productId)){
					date=(Long)device.get("date");
					resp=device;
				}
			}
		}
		if(resp.get("location")==null){
			JSONObject unmarshalled= JSONHelper.getDB(databaseRestTemplateProxy.getRestTemplate(),getDatabaseServiceName()+"/playback/search/findByCustomerIdAndProductIdAndTenantIdAndStatus?customerId="+customerId+"&tenantId="+tenantId+"&status=inprogress&productId="+productId);
			List<JSONObject> plays=(List<JSONObject>)unmarshalled.get("result");
			if(plays==null || plays.size()==0){
				resp.put("location", 0L);
				producer.publish( getPlaybackLocationResponseTopic(), resp.toJSONString());
				return;
			}
			if(locations.get(customerId)==null){
				locations.put(customerId, new HashMap<String,JSONObject>());
			}
			locations.get(customerId).put(plays.get(0).get("deviceId").toString(),plays.get(0));
			producer.publish(getPlaybackLocationResponseTopic(), plays.get(0).toJSONString());
			return;
		}

		producer.publish(getPlaybackLocationResponseTopic(), resp.toJSONString());
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig",topics = { "#{playback.playbackLocationResponseTopic}" }, groupId="playback-response-#random")
	public void playbackLocationClient(Object stream, Object consumer, byte [] evt) throws NoSuchProductException{
		JSONObject json=(JSONObject)JSONValue.parse(new String(evt));
		String handle=json.get("productId").toString()+json.get("customerId").toString();
		if(playbackResponses.get(handle)!=null){
			playbackResponses.get(handle).getValue().add(json);
			playbackResponses.get(handle).getKey().release();
		}
	}
	@Override
	public JSONObject getPlaybackLocation(String productId) throws NoSuchProductException, InterruptedException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		String handle=productId+customerId;
		JSONObject event= JSONHelper.make(new String[]{"customerId", "productId","tenantId"}, new Object[]{customerId,productId,tenantId});
		Semaphore semaphore= new Semaphore(0);
		Pair<Semaphore,List<JSONObject>>pair= new ImmutablePair<Semaphore,List<JSONObject>>(semaphore,new ArrayList<JSONObject>());
		playbackResponses.put(handle, pair);
		producer.publish(getPlaybackLocationRequestTopic(), event.toJSONString());
		semaphore.tryAcquire(getPlaybackResponseMinCount(), getPlaybackResponseTimoutMillis(), TimeUnit.MILLISECONDS);
		List<JSONObject> locations=playbackResponses.get(handle).getRight();
		JSONObject result=new JSONObject();
		for(JSONObject location:locations){
			if(result.size()==0){
				result=location;
			}
			if((Long)result.get("date")>(Long)location.get("date")){
				result=location;
			}
		}
		playbackResponses.remove(handle);
		return result;
	}

	public JSONObject collectPlaybackLocation(String productId, String customerId, Semaphore semaphore) throws NoSuchProductException, InterruptedException {
		String handle=productId+customerId;
		semaphore.tryAcquire(getPlaybackResponseMinCount(), getPlaybackResponseTimoutMillis(), TimeUnit.MILLISECONDS);
		List<JSONObject> locations=playbackResponses.get(handle).getRight();
		JSONObject result=new JSONObject();
		for(JSONObject location:locations){
			if(result.size()==0){
				result=location;
			}
			if((Long)result.get("date")>(Long)location.get("date")){
				result=location;
			}
		}
		playbackResponses.remove(handle);
		return result;
	}

	public void releaseLocationSemaphore(String productId, String customerId) {
		String handle=productId+customerId;
		playbackResponses.remove(handle);
	}

	public Semaphore sendPlaybackLocationRequest(String productId,String customerId, String tenantId) throws NoSuchProductException, InterruptedException {
		String handle=productId+customerId;
		JSONObject event= JSONHelper.make(new String[]{"customerId", "productId","tenantId"}, new Object[]{customerId,productId,tenantId});
		Semaphore semaphore= new Semaphore(0);
		Pair<Semaphore,List<JSONObject>>pair= new ImmutablePair<Semaphore,List<JSONObject>>(semaphore,new ArrayList<JSONObject>());
		playbackResponses.put(handle, pair);
		producer.publish(getPlaybackLocationRequestTopic(), event.toJSONString());
		return semaphore;
	}




	public JSONObject findOfferById(String offerId) throws NoSuchOfferException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject unmarshalled=JSONHelper.getDB(databaseRestTemplateProxy.getRestTemplate(),getDatabaseServiceName()+"/offer/"+offerId);
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			return unmarshalled;
		}
		throw new NoSuchOfferException("No Such Offer");
	}



	protected void checkConcurrentStreams(String productId){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		JSONObject tenant=metadataProvider.getTenantById(tenantId);
		JSONObject drm=(JSONObject)tenant.get("drm");
		if(drm==null){
			log.info("No DRM rule defined for tenant:{}. Aborting concurrent stream check.");
			return;
		}
		Integer maxConcurrentStreamPerProduct=(Integer)drm.get("maxConcurrentStreamPerProduct");
		Integer overallMaxConcurrentStreams=(Integer)drm.get("overallMaxConcurrentStreams");
		Integer concurrentStreamTimeDeltaMillis=(Integer)drm.get("concurrentStreamTimeDeltaMillis");
		if(maxConcurrentStreamPerProduct==null || overallMaxConcurrentStreams==null || concurrentStreamTimeDeltaMillis==null){
			log.info("Malformed concurrent stream rules. Following properties must be defined for each tenant:maxConcurrentStreamPerProduct,overallMaxConcurrentStreams,concurrentStreamTimeDeltaMillis  . Aborting concurrent stream check.");
			return;
		}
		int totalConcurrentStreams=0;
		int productConcurrentStreams=0;
		Map<String, JSONObject> customer=locations.get(customerId);
		Long limit=System.currentTimeMillis()-concurrentStreamTimeDeltaMillis;
		for(String deviceId:customer.keySet()){
			JSONObject heartbeat=customer.get(deviceId);
			if((Long)heartbeat.get("date")>limit){
				if(((String)heartbeat.get("productId")).equalsIgnoreCase(productId)){
					productConcurrentStreams++;
				}
				totalConcurrentStreams++;
			}
		}
		log.info("Number of concurrent streams for user {}:{}",customerId,totalConcurrentStreams);
		if(totalConcurrentStreams > overallMaxConcurrentStreams){
			throw new IllegalStateException("Overall max number of concurrent streams reached");
		}
		log.info("Number of concurrent streams for user {} on product {}:{}",customerId,productId,productConcurrentStreams);
		if(productConcurrentStreams >maxConcurrentStreamPerProduct){
			throw new IllegalStateException("Product max number of concurrent streams reached");
		}
	}
	@Override
	public JSONObject getPlaybackURL(String productId, String offerId,	String deviceSpec, String deviceId) throws NoSuchOfferException, NoSuchProductException, NoMediaFoundException, NoDeviceSpecMappingException, NoSuchEntitlementException, InterruptedException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		Semaphore locationSemaphore=sendPlaybackLocationRequest(productId, customerId, tenantId);
		checkConcurrentStreams(productId);
		JSONObject offer=findOfferById(offerId);
		JSONObject product=metadataProvider.getProductDetails(productId,offer);
		List<JSONObject>mediaList=(List<JSONObject>)product.get("mediaList");
		String spec=null;
		for(String  regex:deviceSpecMapping.keySet()){
			if(Pattern.matches(regex, deviceSpec)){
				spec=deviceSpecMapping.get(regex);
			}
		}
		if(StringUtils.isEmpty(spec)){
			releaseLocationSemaphore(productId,customerId);
			throw new NoDeviceSpecMappingException();
		}
		JSONObject asset=null;
		spec=spec.toLowerCase();
		JSONObject location=collectPlaybackLocation(productId, customerId, locationSemaphore);
		for(JSONObject media:mediaList){
			if( Pattern.matches(spec,media.get("aspectRatio").toString().toLowerCase())  ||
				Pattern.matches(spec,media.get("targetDevice").toString().toLowerCase()) ||
				Pattern.matches(spec,media.get("url").toString().toLowerCase()) ||
				Pattern.matches(spec,media.get("screenFormat").toString().toLowerCase()) ){
				media.putAll(location);
				asset=media;
			}
		}
		if(asset==null){
			releaseLocationSemaphore(productId,customerId);
			throw new NoMediaFoundException("No media found matching device spec");
		}
		if(offer.get("offerType").toString().equalsIgnoreCase("free")){
			return JSONHelper.make(new String[]{"entitlementId","location","url","productId","offerId","componentId"},new Object[]{"free",asset.get("location"),asset.get("url"),asset.get("productId"),offer.get("id"),asset.get("componentId")} );
		}
		Map<String,String>urlVarialbles= new HashMap<String,String>();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		headers.set("Content-type","application/json");
		HttpEntity<String> entity = new HttpEntity<String>("{}",headers);
		ResponseEntity<JSONObject> entitlement=databaseRestTemplateProxy.getRestTemplate().exchange(getEntitlementServiceUrlTemplate().replace("{productId}", productId).replace("{tenantId}", tenantId).replace("{customerId}", customerId), HttpMethod.GET, entity, JSONObject.class, urlVarialbles);
		if(entitlement==null || entitlement.getBody()==null){
			releaseLocationSemaphore(productId,customerId);
			throw new NoSuchEntitlementException("No entitlement on this product");
		}
		asset.putAll(entitlement.getBody());
		return JSONHelper.make(new String[]{"entitlementId","location","url","productId","offerId","componentId"},new Object[]{entitlement.getBody().get("id"),asset.get("location"),asset.get("url"),asset.get("productId"),offer.get("id"),asset.get("componentId")} );
	}

	@Override
	public void completeWatchingTitle(String productId) {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String customerId=(String)credentials.get("customerId");
		Map<String, JSONObject>heartbeats=locations.remove(customerId);

		JSONObject unmarshalled= JSONHelper.getDB(databaseRestTemplateProxy.getRestTemplate(),getDatabaseServiceName()+"/playback/search/findByCustomerIdAndProductIdAndTenantIdAndStatus?customerId="+customerId+"&tenantId="+tenantId+"&status=inprogress&productId="+productId);
		List<JSONObject> playbacks=(List<JSONObject>)unmarshalled.get("result");
		if(playbacks!=null || playbacks.size()!=0){
			for(JSONObject playback:playbacks){
				playback.put("status", "completed");
				String id=(String)playback.get("id");
				JSONObject playbackRecent=heartbeats.remove(playback.get("deviceId"));
				if(playbackRecent!=null){
					playback.putAll(playbackRecent);
				}
				databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/playback/"+id, JSONHelper.marshall(playback));
			}
		}
		for(JSONObject heartbeat:heartbeats.values()){
			heartbeat.put("status", "completed");
			String id=heartbeat.get("id")==null?UUID.randomUUID().toString():(String)heartbeat.get("id");
			databaseRestTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/playback/"+id, JSONHelper.marshall(heartbeat));
		}
	}

}
