package com.libertas.vipaas.services.rating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.metadata.MetadataProvider;

@Component
@Configuration
@ConfigurationProperties("rating")
@Slf4j
public class AvgUserRatingComputingConsumer implements Runnable{


	private String avgUserRatingServiceUrl;
	private BlockingQueue<JSONObject > ratingQueue;
	private MetadataProvider metadataProvider;
	@Autowired
	private RestTemplateProxy productServiceRestTemplateProxy;

	public BlockingQueue<JSONObject> getRatingQueue() {
		return ratingQueue;
	}


	public void setRatingQueue(BlockingQueue<JSONObject> ratingQueue) {
		this.ratingQueue = ratingQueue;
	}


	public String getAvgUserRatingServiceUrl() {
		return avgUserRatingServiceUrl;
	}


	public void setAvgUserRatingServiceUrl(String avgUserRatingServiceUrl) {
		this.avgUserRatingServiceUrl = avgUserRatingServiceUrl;
	}


	public MetadataProvider getMetadataProvider() {
		return metadataProvider;
	}


	public void setMetadataProvider(MetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		Map<String,JSONObject>ratings= new HashMap<String,JSONObject>();

		while(!ratingQueue.isEmpty()){
			JSONObject json=ratingQueue.poll();
			String productId=(String)json.get("productId");
			if(ratings.get(productId)==null){
				ratings.put(productId, new JSONObject());
				ratings.get(productId).put("tenantId",json.get("tenantId"));
			}
			if(ratings.get(productId).get("rating")==null){
				try {
					List<JSONObject>  products=metadataProvider.findProductFieldsByProductId(productId, "avgUserRating","avgUserRatingCount");
					ratings.get(productId).put("rating", products.get(0).get("avgUserRating")==null?0:Double.parseDouble((String)products.get(0).get("avgUserRating")));
					ratings.get(productId).put("count", products.get(0).get("avgUserCount")==null?0:Integer.parseInt((String)products.get(0).get("avgUserRatingCount")));
				} catch (NoSuchProductException e) {
					log.warn(e.getMessage(),e);
				}
		    }
			if(ratings.get(productId).get("count")!=null && ratings.get(productId).get("rating")!=null && json.get("ratingValue")!=null){
				ratings.get(productId).put(productId, (((Long)(ratings.get(productId).get("rating"))*(Long)(ratings.get(productId).get("count")))+(Integer)json.get("ratingValue"))/((Long)(ratings.get(productId).get("count"))+1));
				ratings.get(productId).put("count", (Long)(ratings.get(productId).get("count"))+1);
			}
		}
		for(String productId:ratings.keySet()){
			try {
				JSONObject o= new JSONObject();
				o.put("productId",productId);
				o.put("avgUserRating",ratings.get(productId).get("rating"));
				o.put("avgUserRatingCount",ratings.get(productId).get("count"));
				productServiceRestTemplateProxy.getRestTemplate().postForLocation(getAvgUserRatingServiceUrl()+"?tenantId="+ratings.get(productId).get("tenantId"), Arrays.asList(o));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		ratings.clear();
	}

}
