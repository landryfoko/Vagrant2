package com.libertas.vipaas.common.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JSONHelper {
	
	public static JSONObject getDB(RestTemplate template, String url){
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> json=template.exchange(url,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled=JSONHelper.unmarshall(json.getBody());
		return unmarshalled;
	}
	public static JSONObject unmarshall(JSONObject content){
		if(content.get("extension")!=null){
			JSONObject result= (JSONObject)JSONValue.parse((String)content.get("extension"));
			if(StringUtils.isNotEmpty((String)content.get("id"))){
				result.put("id", content.get("id"));
			}
			return result;
		}else if(content.get("_embedded")!=null){
			Map embedded=(Map)content.get("_embedded");
			Map page=(Map)content.get("page");
			List oldEntries=(List)embedded.values().iterator().next();
			List<Object> newEntries = new ArrayList<Object>();
			for(Object entry:oldEntries){
				Map jsonEntry=(Map)entry;
				if(jsonEntry.get("extension")!=null){
					String extension=(String)jsonEntry.get("extension");
					if(extension!=null){
						JSONObject jsonExtension=(JSONObject)JSONValue.parse(extension);
						if(StringUtils.isNotEmpty((String)jsonEntry.get("id"))){
							jsonExtension.put("id", jsonEntry.get("id"));
						}
						newEntries.add(jsonExtension);
					}
				}
		}
			JSONObject result= new JSONObject();
			if(page!=null){
				Integer start=(Integer)page.get("size")*(Integer)page.get("number");
				result.put("pageInfo", JSONHelper.make(new String[]{"total","start","end"}, new Object[]{page.get("totalElements"),start ,start+newEntries.size()}));
			}
			result.put("result", newEntries);
			return result;
	}else{
		content.remove("extension");
		content.remove("_links");
		return content;
	}
	}
	
	public static JSONObject marshall(JSONObject object){
		JSONObject clone= new JSONObject(object);
		clone.put("extension", clone.toJSONString());
		return clone;
	}
	public static JSONObject make(String key,Object value){
		JSONObject json= new JSONObject();
		json.put(key, value);
		return json;
	}
	public static JSONObject make(String keys[],Object values[]){
		JSONObject json= new JSONObject();
		for(int count=0; count<keys.length; count++){
			json.put(keys[count], values[count]);
		}
		return json;
	}
}
