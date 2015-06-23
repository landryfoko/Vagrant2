package com.libertas.vipaas.services.genre;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateGenreException;
import com.libertas.vipaas.common.exceptions.NoSuchGenreException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("genre")
public class GenreServiceImpl implements GenreService {
	@Autowired
	MetadataProvider metadataProvider;

	@Autowired
	RestTemplateProxy restTemplateProxy;

	private String databaseServiceName;
	//@Autowired
	private ProducerTemplate producer;

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject createGenre(String name, JSONObject metadata) throws DuplicateGenreException  {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String customerId=(String)credentials.get("customerId");
		String tenantId=(String)credentials.get("tenantId");
		if(existsGenreByName(name)){
			throw new DuplicateGenreException("Genre already exists");
		}
		metadata.remove("id");
		JSONObject genre= new JSONObject(metadata);
		genre.put("name", name);
		genre.put("customerId", customerId);
		genre.put("tenantId", tenantId);
		genre.put("id", UUID.randomUUID().toString());
		restTemplateProxy.getRestTemplate().postForLocation(getDatabaseServiceName()+"/genre", JSONHelper.marshall(genre));
		return metadataProvider.filter(genre);
	}

	private JSONObject getGenreByIdInternal(String genreId) throws NoSuchGenreException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> bookmarks=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/genre/"+genreId,HttpMethod.GET,entity, JSONObject.class);

		JSONObject unmarshalled=JSONHelper.unmarshall(bookmarks.getBody());
		if(tenantId.equals((String)unmarshalled.get("tenantId"))){
			metadataProvider.filter(unmarshalled);
			return unmarshalled;
		}
		throw new NoSuchGenreException("No Such Genre");
	}

	@Override
	public JSONObject getGenreById(String genreId) throws NoSuchGenreException {
		JSONObject genre=getGenreByIdInternal(genreId);
		return metadataProvider.filter(genre);
	}
	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, String sortField, String sortOrder) {
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+","+sortOrder);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> genres=restTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/genre/search/findByTenantId?tenantId="+tenantId+"&size="+pageSize+"&page="+pageNumber+sort,HttpMethod.GET,entity, JSONObject.class);
		JSONObject unmarshalled= JSONHelper.unmarshall(genres.getBody());
		metadataProvider.filter((List<JSONObject>)unmarshalled.get("result"));
		return unmarshalled;
	}

	@Override
	public void updateGenre(String genreId, JSONObject metadata) throws NoSuchGenreException {
		JSONObject oldGenre=getGenreByIdInternal(genreId);
		if(oldGenre==null ){
			throw new NoSuchGenreException("No such genre");
		}
		metadata.remove("id");
		metadata.remove("tenantId");
		metadata.remove("customerId");
		oldGenre.putAll(metadata);
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/genre/"+oldGenre.get("id"), JSONHelper.marshall(oldGenre));

	}

	@Override
	public void deleteGenreById( String genreId) throws NoSuchGenreException {
		JSONObject genre=getGenreByIdInternal(genreId);
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		genre.put("tenantId", tenantId+"_DELETION-TAG:"+UUID.randomUUID().toString());
		genre.put("deleteDate", new Date().toString());
		restTemplateProxy.getRestTemplate().put(getDatabaseServiceName()+"/genre/"+genre.get("id"), JSONHelper.marshall(genre));
	}

	private boolean existsGenreByName(String name){
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject genre=restTemplateProxy.getRestTemplate().getForObject(getDatabaseServiceName()+"/genre/search/findByNameAndTenantId?name="+name+"&tenantId="+tenantId, JSONObject.class);
		if(genre==null){
			return false;
		}
		return genre!=null && genre.size()>0 && ((List)genre.get("links")).size()>0;
	}

}
