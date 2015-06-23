package com.libertas.vipaas.services.product;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.libertas.vipaas.common.cloud.rest.api.RestTemplateProxy;
import com.libertas.vipaas.common.exceptions.DuplicateProductException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

@Service
@ConfigurationProperties("product")
@Slf4j
public class ProductServiceImpl implements ProductService {


	//@Autowired
	private ProducerTemplate producer;
	@Autowired
	private RestTemplateProxy solrReadRestTemplateProxy;
	@Autowired
	private RestTemplateProxy solrWriteRestTemplateProxy;
	@Autowired
	private RestTemplateProxy entitlementRestTemplateProxy;
	@Autowired
	private RestTemplateProxy databaseRestTemplateProxy;
	@Autowired
	private RestTemplateProxy playbackServiceRestTemplateProxy;
	@Autowired
	private RestTemplateProxy entitlementServiceRestTemplateProxy;
	//@Autowired
	private CuratorFramework curatorFramework;
	private String solrReadUrl;
	private String solrWriteUrl;
	private List<String> defaultSearcheableFields;
	private String defaultQueryTemplate;
	private String databaseServiceName;
	private String tenantServiceName;
	private String offerServiceName;
	private String createProductLockPath;
	private Integer createProductLockTimeoutMillis;
	private ExecutorService executor = Executors.newCachedThreadPool();

	@Autowired
	private MetadataProvider metadataProvider;

	private String progressWatchedQueryTemplate;
	private String canWatchNowQueryTemplate;

	public String getProgressWatchedQueryTemplate() {
		return progressWatchedQueryTemplate;
	}

	public void setProgressWatchedQueryTemplate(String progressWatchedQueryTemplate) {
		this.progressWatchedQueryTemplate = progressWatchedQueryTemplate;
	}

	public String getCanWatchNowQueryTemplate() {
		return canWatchNowQueryTemplate;
	}

	public void setCanWatchNowQueryTemplate(String canWatchNowQueryTemplate) {
		this.canWatchNowQueryTemplate = canWatchNowQueryTemplate;
	}

	public Integer getCreateProductLockTimeoutMillis() {
		return createProductLockTimeoutMillis;
	}

	public void setCreateProductLockTimeoutMillis(
			Integer createProductLockTimeoutMillis) {
		this.createProductLockTimeoutMillis = createProductLockTimeoutMillis;
	}

	public String getCreateProductLockPath() {
		return createProductLockPath;
	}

	public void setCreateProductLockPath(String createProductLockPath) {
		this.createProductLockPath = createProductLockPath;
	}

	public String getOfferServiceName() {
		return offerServiceName;
	}

	public void setOfferServiceName(String offerServiceName) {
		this.offerServiceName = offerServiceName;
	}

	public String getTenantServiceName() {
		return tenantServiceName;
	}

	public void setTenantServiceName(String tenantServiceName) {
		this.tenantServiceName = tenantServiceName;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}


	public String getDefaultQueryTemplate() {
		return defaultQueryTemplate;
	}

	public void setDefaultQueryTemplate(String defaultQueryTemplate) {
		this.defaultQueryTemplate = defaultQueryTemplate;
	}

	public List<String> getDefaultSearcheableFields() {
		return defaultSearcheableFields;
	}

	public void setDefaultSearcheableFields(List<String> defaultSearcheableFields) {
		this.defaultSearcheableFields = defaultSearcheableFields;
	}


	public String getSolrReadUrl() {
		return solrReadUrl;
	}

	public void setSolrReadUrl(String solrReadUrl) {
		this.solrReadUrl = solrReadUrl;
	}

	public String getSolrWriteUrl() {
		return solrWriteUrl;
	}

	public void setSolrWriteUrl(String solrWriteUrl) {
		this.solrWriteUrl = solrWriteUrl;
	}

	@Override
	public JSONObject createProduct(String bindId, JSONObject metadata) throws DuplicateProductException  {
		if(curatorFramework==null){
			JSONObject blob=createProductNoLock(bindId, metadata);
			return blob;
		}
		else{
			InterProcessMutex lock= new InterProcessMutex(curatorFramework, getCreateProductLockPath());
			try{
					if ( !lock.acquire(getCreateProductLockTimeoutMillis(), TimeUnit.MILLISECONDS) )
					{
						throw new IllegalStateException("CreateProduct could not acquire the lock");
					}
					log.info("Lock acquired for creating product with bind Id:{}",bindId);
					JSONObject blob= createProductNoLock(bindId, metadata);
					return metadataProvider.filter(blob);
	        }
			catch(Exception e){
				log.error(e.getMessage(),e);
				throw new IllegalStateException("Could not create product");
			}finally{
	        	try {
					lock.release();
					log.info("Lock released after for creating product with bind Id:{}",bindId);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
	        }
		}
	}

	private JSONObject createProductNoLock(String bindId, JSONObject metadata) throws DuplicateProductException  {
		log.info("Creating product with URL:{}",getSolrWriteUrl());
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String productId=extractOrCreateProductId(bindId);
		postMetadataToSolr(bindId,productId, tenantId, metadata);
		JSONObject blob=updateSolrBlob(productId,tenantId);
		return blob;

	}


	private String extractOrCreateProductId(String bindId){
		String s=solrWriteRestTemplateProxy.getRestTemplate().getForObject(getSolrReadUrl()+"?wt=json&fl=id&q=bindId:"+bindId, String.class);
		log.info("Solr query result:"+s);
		JSONObject result=(JSONObject)JSONValue.parse(s);
		if(result!=null && result.get("response")!=null  && ((JSONObject)result.get("response")).get("numFound")!=null){
			List<JSONObject> list=(List<JSONObject>)((JSONObject)result.get("response")).get("docs");
			if(list!=null && list.size()>0 && list.get(0).get("id")!=null){
				return (String)list.get(0).get("id");
			}
		}
		return UUID.randomUUID().toString();
	}

	private JSONObject updateSolrBlob(String productId, String tenantId){
		String productPieces=solrWriteRestTemplateProxy.getRestTemplate().getForObject(getSolrReadUrl()+"?wt=json&fl=avgUserRating,blob,details,images,previews,videos,tags&q=id:"+productId+" AND apiKey:"+tenantId, String.class);
		JSONObject productJson=(JSONObject)JSONValue.parse(productPieces);
		log.info("Product parts:{}",productJson);
		JSONObject blob=null;
		if(productJson.get("response")!=null && ((JSONObject)productJson.get("response")).get("docs")!=null){
			List docs=(List)((JSONObject)productJson.get("response")).get("docs");
			if(docs.size()>0){
				List blobs=(List)((Map)docs.get(0)).get("blob");
				List details=(List)((Map)docs.get(0)).get("details");
				List images=(List)((Map)docs.get(0)).get("images");
				List previews=(List)((Map)docs.get(0)).get("previews");
				List videos=(List)((Map)docs.get(0)).get("videos");
				List tags=(List)((Map)docs.get(0)).get("tags");
				List avgUserRating=(List)((Map)docs.get(0)).get("avgUserRating");
				List avgUserRatingCount=(List)((Map)docs.get(0)).get("avgUserRatingCount");

				blob=(JSONObject)JSONValue.parse((String)blobs.get(0));
				JSONObject detail =(JSONObject)JSONValue.parse((String)details.get(0));
				log.info("Blob:"+blob);
				log.info("Details:"+detail);
				if(blob!=null){
					if(images!=null){
						blob.put("imageList", unmarshalList(images));
					}
					if(previews!=null){
						blob.put("previews", unmarshalList(previews));
					}
					if(tags!=null){
						blob.put("tags", tags);
					}
					if(avgUserRating!=null && avgUserRating.size()>0){
						blob.put("avgUserRating", avgUserRating.get(0));
					}
					if(avgUserRatingCount!=null && avgUserRatingCount.size()>0){
						blob.put("avgUserRatingCount", avgUserRatingCount.get(0));
					}
				}
				if(details!=null){
					if(images!=null){
						detail.put("imageList", unmarshalList(images));
					}
					if(previews!=null){
						detail.put("previewList", unmarshalList(previews));
					}
					if(videos!=null){
						detail.put("mediaList", unmarshalList(videos));
					}
					if(tags!=null){
						detail.put("tags", tags);
					}
					if(avgUserRating!=null && avgUserRating.size()>0){
						detail.put("avgUserRating", avgUserRating.get(0));
					}
					if(avgUserRatingCount!=null && avgUserRatingCount.size()>0){
						detail.put("avgUserRatingCount", avgUserRatingCount.get(0));
					}
				}
				blob=(JSONObject)toCamelCase(blob);
				detail=(JSONObject)toCamelCase(detail);
				JSONObject doc=json("add",json("doc",json(new String[]{"blob","id","details"},new Object[]{json("set",blob.toJSONString()),productId,json("set",detail.toJSONString())})));
				log.info("Posting updated product to solr:{}",doc.toJSONString());
				solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",doc);
			}else{
				log.warn("No SOLR DOCS element found for product :{}",productId);
			}
		}else{
			log.warn("No SOLR DOCS element found for product :{}",productId);
		}
		return blob;
	}
	private List<Object> unmarshalList(List<Object> list){
		for(int i=0; i<list.size(); i++){
			list.set(i, JSONValue.parse((String)list.get(i)));
		}
		return list;
	}
	@Override
	public void addVideoToProduct(String productId, JSONObject asset) throws MissingFieldException, NoSuchProductException{
		addAssetToProduct(productId, asset,"videos");
	}
	@Override
	public void addPreviewToProduct(String productId, JSONObject asset) throws MissingFieldException, NoSuchProductException{
		addAssetToProduct(productId, asset,"previews");
	}
	private void addAssetToProduct(String productId, JSONObject asset, String type) throws MissingFieldException, NoSuchProductException{
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		if(!existsProduct(productId, tenantId)){
			throw new NoSuchProductException("No such product");
		}
		String aspectRatio=(String)asset.get("aspectRatio");
		if(StringUtils.isEmpty(aspectRatio)){
			throw new MissingFieldException("aspectRation");
		}
		String targetDevice=(String)asset.get("targetDevice");
		if(StringUtils.isEmpty(targetDevice)){
			throw new MissingFieldException("targetDevice");
		}
		String screenFormat=(String)asset.get("screenFormat");
		if(StringUtils.isEmpty(screenFormat)){
			throw new MissingFieldException("screenFormat");
		}
		String url=(String)asset.get("url");
		if(StringUtils.isEmpty(url)){
			throw new MissingFieldException("url");
		}
		asset.put("componentId", UUID.randomUUID().toString());
		JSONObject doc=json("add",json("doc",json(new String[]{"id",type},new Object[]{productId,json("add",asset)})));
		log.info("Posting updated product to solr:{}",doc.toJSONString());
		solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",doc);
		updateSolrBlob(productId, tenantId);
	}


	@Override
	public void addImageToProduct(String productId, JSONObject asset) throws MissingFieldException, NoSuchProductException{
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		if(!existsProduct(productId, tenantId)){
			throw new NoSuchProductException("N o such product");
		}
		String imageWidth=(String)asset.get("imageWidth");
		if(StringUtils.isEmpty(imageWidth)){
			throw new MissingFieldException("imageWidth");
		}
		String imageHeight=(String)asset.get("imageHeight");
		if(StringUtils.isEmpty(imageHeight)){
			throw new MissingFieldException("imageHeight");
		}
		String imageURL=(String)asset.get("imageURL");
		if(StringUtils.isEmpty(imageURL)){
			throw new MissingFieldException("imageURL");
		}
		String imageType=(String)asset.get("imageType");
		if(StringUtils.isEmpty(imageType)){
			throw new MissingFieldException("imageType");
		}
		asset.put("componentId", UUID.randomUUID().toString());
		JSONObject doc=json("add",json("doc",json(new String[]{"id","images"},new Object[]{productId,json("add",asset)})));
		log.info("Posting updated product to solr:{}",doc.toJSONString());
		solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",doc);
		updateSolrBlob(productId, tenantId);
	}

	private void postMetadataToSolr(String bindId, String productId, String tenantId, JSONObject metadata){
		JSONObject data=(JSONObject)toCamelCase(metadata);
		data.remove("purchaseOptionList");
		data.put("id", productId);
		data.remove("productId");
		data.put("bindId", bindId);
		List<Object> emptyFields= new ArrayList<Object>();
		for(Object key:data.keySet()){
			if(data.get(key)==null || (data.get(key) instanceof List && ((List)data.get(key)).size()==0 )){
				emptyFields.add(key);
			}
		}
		for(Object key:emptyFields){
			data.remove(key);
		}
		String blob=data.toJSONString();
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		JSONObject tenant=(JSONObject)credentials.get("tenant");
		JSONObject product= new JSONObject();
		for(Object key:metadata.keySet()){
			for(String field:defaultSearcheableFields){
				if(key.toString().toLowerCase().equals(field)){
					JSONObject entry= new JSONObject();
					entry.put("add", metadata.get(key));
					product.put(field.toLowerCase(), entry);
				}
			}
		}
		product.putAll(json(new String[]{"blob","bindId","apiKey","id","details"},new Object[]{blob,bindId,tenantId,productId,blob}) );
		JSONObject result=json("add",json("doc",product));
		log.info("Posting product to solr:{}",result.toJSONString());
		solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",result);
	}

	private static Object toCamelCase(Object metadata){
		if(metadata instanceof List){
			List<Object> elements= new ArrayList<Object>();
			List<Object> metadataList= (List<Object>)metadata;
			for(Object data:metadataList){
				if(data instanceof List){
					elements.add(toCamelCase((List)data));
				}else if(data instanceof Map){
					elements.add(toCamelCase((Map)data));
				}else{
					elements.add(data);
				}
			}
			return elements;
		}
		if(metadata instanceof Map){
			JSONObject map= new JSONObject();
			Map metadataJSON= (Map)metadata;
			for(Object key:metadataJSON.keySet()){
				String keyStr=key.toString();
				String camelized=null;
				if(keyStr.equals(keyStr.toUpperCase())){
					camelized=keyStr;
				}else if(keyStr.toLowerCase().equals("id")){
					camelized="id";
				}else if(keyStr.toLowerCase().endsWith("id")){
					camelized=(keyStr.charAt(0)+"").toLowerCase()+keyStr.substring(1);
					camelized=camelized.substring(0, camelized.length()-2)+"Id";
				}else{
					camelized=(keyStr.charAt(0)+"").toLowerCase()+keyStr.substring(1);
				}
				map.put(camelized, toCamelCase(metadataJSON.get(key)));
			}
			return map;
		}

		return metadata;
	}



	@Override
	public JSONObject getProductDetails(final String productId) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		final String tenantId=(String)credentials.get("tenantId");
		final String customerId=(String)credentials.get("customerId");
		Callable<Boolean> canWatchNow=null;
		Callable<Long> progressWatched=null;
		if(StringUtils.isNotBlank(customerId)){
			canWatchNow= new Callable<Boolean>(){
				@Override
				public Boolean call() throws Exception {
					String url=getCanWatchNowQueryTemplate()
									.replace("{tenantId}",tenantId)
									.replace("{customerId}",customerId)
									.replace("{productId}",productId);
					JSONObject rawResult=entitlementServiceRestTemplateProxy.getRestTemplate().getForObject(url, JSONObject.class);
					return rawResult !=null && rawResult.size()!=0;
				}

			};
			progressWatched= new Callable<Long>(){
				@Override
				public Long call() throws Exception {
					String url=getProgressWatchedQueryTemplate()
							.replace("{tenantId}",tenantId)
							.replace("{customerId}",customerId)
							.replace("{productId}",productId);
					JSONObject rawResult=playbackServiceRestTemplateProxy.getRestTemplate().getForObject(url, JSONObject.class);
					return rawResult==null?0:Long.parseLong(""+rawResult.get("location"));
				}

			};
			executor.submit(canWatchNow);
			executor.submit(progressWatched);
		}
		String solrUrl=getSolrReadUrl()+"?wt=json&fl=details,offers,videos&q=id:"+productId+" AND apiKey:"+tenantId;
		String rawResult=solrReadRestTemplateProxy.getRestTemplate().getForObject(solrUrl, String.class);
		JSONObject jsonResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)(((JSONObject)jsonResult.get("response")).get("docs"));
		if(docs.size()==0){
			throw new NoSuchProductException("No such product");
		}
		JSONObject jsonDoc=(JSONObject)docs.get(0);
		if(jsonDoc.get("offers")==null){
			log.warn("No offers found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No offers found in product");
		}
		List<String> offerIds=(List<String>)(jsonDoc.get("offers"));
		if(jsonDoc.get("details")==null){
			log.warn("No details found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No details found in product");
		}
		String detailStrs=(((List<String>)jsonDoc.get("details")).get(0));
		if(jsonDoc.get("videos")==null){
			log.warn("No videos found in product:{}. Cannot compute its product details.",productId);
			throw new IllegalStateException("No videos found in product");
		}
		List<String> videosStr=(List<String>)jsonDoc.get("videos");

		JSONObject details=(JSONObject)JSONValue.parse(detailStrs);
		List<JSONObject> videos=new ArrayList<JSONObject>(videosStr.size());
		for(String videoStr:videosStr){
			JSONObject video=(JSONObject)JSONValue.parse(videoStr);
			if(video.get("url")!=null &&  StringUtils.isNotBlank(video.get("url").toString())){
				videos.add(video);
			}else{
				log.info("Video has no URL in product {}: Ignoring it.{}",productId,video);
			}
		}
		if(videos.size()==0){
			log.warn("No videos found in product:{} with valid URL. Cannot compute its product details.",productId);
			throw new IllegalStateException("No videos found in product");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> rawOffers=databaseRestTemplateProxy.getRestTemplate().exchange(getDatabaseServiceName()+"/offer/search/findByIdIn?ids="+StringUtils.join(offerIds, ','),HttpMethod.GET,entity, JSONObject.class);
		JSONObject offers=JSONHelper.unmarshall(rawOffers.getBody());
		if(offers==null || offers.get("result")==null){
			throw new IllegalStateException("Offers seem to be malformed as it could not be parsed:"+StringUtils.join(offerIds, ','));
		}
		List<JSONObject> offerList=(List<JSONObject>)offers.get("result");
		if(offerList.size()==0){
			throw new IllegalStateException("Somehow no offer could be found in DB with ids:"+StringUtils.join(offerIds, ','));
		}
		details.remove("mediaList");
		JSONArray purchaseOptionList= new JSONArray();
		for(JSONObject offer:offerList){
			JSONArray mediaList= new JSONArray();
			offer.put("mediaList", mediaList);
			String regex=(String)offer.get("regex");
			for(JSONObject video:videos){
				String targetDevice=(String)video.get("targetDevice");
				String aspectRatio=(String)video.get("aspectRatio");
				String screenFormat=(String)video.get("screenFormat");
				String url=(String)video.get("url");
				if(StringUtils.isEmpty(url) || StringUtils.isEmpty(aspectRatio)  || StringUtils.isEmpty(targetDevice) || StringUtils.isEmpty(screenFormat)){
					log.info("Asset with component Id does not have valid url, aspectRatio, targetDevice, screenFormat. Cannot add it to product details.{}",video.get("componentId"));
				}else if(java.util.regex.Pattern.matches(regex,url)  ||
						   java.util.regex.Pattern.matches(regex,aspectRatio)  ||
						   java.util.regex.Pattern.matches(regex,targetDevice) ||
						   java.util.regex.Pattern.matches(regex,screenFormat) ){
					JSONObject media= new JSONObject(video);
					media.remove("url");
					mediaList.add(media);
				}
			}
			if(mediaList.size()>0){
				offer.remove("regex");
				offer.remove("apiKey");
				offer.remove("customerId");
				offer.remove("tenantId");
				offer.put("mediaList",mediaList);
				purchaseOptionList.add(offer);
			}else{
				log.info("No asset found that match foolowing offer with id:{}",offer.get("id"));
			}
		}
		details.remove("videos");
		details.put("purchaseOptionList",purchaseOptionList);
		if(canWatchNow!=null && progressWatched!=null){
			try {
				details.put("canWatchNow", canWatchNow.call());
				details.put("progressWatched", progressWatched.call());
			} catch (Exception e) {
				details.put("canWatchNow", false);
				details.put("progressWatched", -1);
				log.info(e.getMessage(),e);
			}
		}else{
			details.put("canWatchNow", false);
			details.put("progressWatched", -1);
		}
		return metadataProvider.filter(details);
	}

	protected JSONObject search(String tenantId, String queryTemplate, JSONObject query, Integer pageSize, Integer pageNumber,String sortField,String sortOrder){
		String template=StringUtils.isEmpty(queryTemplate)?getDefaultQueryTemplate():queryTemplate;
		String expandedQuery=template.replace("{}", (String)query.get("query"));
		expandedQuery=expandedQuery+(StringUtils.isEmpty((String)query.get("filter"))?"":(" AND "+query.get("filter")));
		JSONObject finalResult=search(expandedQuery, tenantId, pageSize, pageNumber,sortField,sortOrder);
		return metadataProvider.filter(finalResult);
	}

	protected JSONObject search(String expandedQuery,String tenantId, Integer pageSize, Integer pageNumber, String sortField, String sortOrder){
		String sort=StringUtils.isEmpty(sortField)|| StringUtils.isEmpty(sortOrder)?"":("&sort="+sortField+" "+sortOrder);
		Integer start=pageNumber*pageSize;
		String url=getSolrReadUrl()+"?wt=json&fl=blob&start="+start+"&rows="+pageSize+"&q=("+expandedQuery+") AND apiKey:"+tenantId+sort;
		String rawResult=solrReadRestTemplateProxy.getRestTemplate().getForObject(url, String.class);
		JSONObject jsonResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)(((JSONObject)jsonResult.get("response")).get("docs"));
		List preliminaryResult= new ArrayList<>();
		for(Object doc:docs){
			JSONObject jsonDoc=(JSONObject)doc;
			String blob=(String)(((List)jsonDoc.get("blob")).get(0));
			preliminaryResult.add(JSONValue.parse(blob));
		}
		JSONObject finalResult= new JSONObject();
		finalResult.put("result", preliminaryResult);
		JSONObject pageInfo= JSONHelper.make(new String[]{"total","start","end"},  new Object[]{(Long)(((JSONObject)jsonResult.get("response")).get("numFound")),start,start+docs.size()});
		finalResult.put("pageInfo", pageInfo);
		return metadataProvider.filter(finalResult);
	}

	@Override
	public JSONObject findAll(Integer pageSize,	Integer pageNumber, List<String> tags, String sortField, String sortOrder) throws MissingFieldException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");

		StringBuffer tagsQuery= new StringBuffer();
		if(tags==null || tags.size()==0){
			throw new MissingFieldException("tags");
		}
		for(String tag:tags){
			tagsQuery.append(" OR  tags:").append(tag);
		}
		if(tagsQuery.length()>4){
			tagsQuery.delete(0, 5);
		}else{
			throw new MissingFieldException("tags");
		}
		JSONObject result=search(tagsQuery.toString(), tenantId, pageSize, pageNumber, sortField, sortOrder);
		return metadataProvider.filter(result);
	}

	@Override
	public void updateProduct(String productId, JSONObject metadata) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		updateSolrBlob(productId, tenantId);
	}

	@Override
	public void deleteProductById( String productId) throws NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		String url=getSolrReadUrl()+"?wt=json&fl=id&q=apiKey:"+tenantId+" AND id:"+productId;
		String rawResult=solrWriteRestTemplateProxy.getRestTemplate().getForObject(url, String.class);

		JSONObject jsonRawResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)((JSONObject)jsonRawResult.get("response")).get("docs");
		if(docs.size()==0){
			throw new NoSuchProductException("No such product");
		}
		Object l=json("add",json("doc",json(new String[]{"id","apiKey"},new Object[]{productId,json("set",tenantId+"_DELETION_TAG"+UUID.randomUUID().toString())})));
		log.info("Pushing deletion request to solr:{}",l);
		solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",l);
	}



	public void untag(String productId, List<String>  tagsRequest, String tagName) throws MissingFieldException, NoSuchProductException {
		if(tagsRequest.size()==0  ){
			throw new MissingFieldException(tagName);
		}
		Set<String> tags=new HashSet<String>(toLowerCase(tagsRequest));
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		if(!existsProduct(productId, tenantId)){
			throw new NoSuchProductException("No such product");
		}
		String url=getSolrReadUrl()+"?wt=json&fl="+tagName+"&q=apiKey:"+tenantId+" AND id:"+productId;
		String rawResult=solrWriteRestTemplateProxy.getRestTemplate().getForObject(url, String.class);

		JSONObject jsonRawResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)((JSONObject)jsonRawResult.get("response")).get("docs");
		if(docs.size()!=0){
			List currenTags=toLowerCase((List)((JSONObject)docs.get(0)).get(tagName));
			if(currenTags!=null){
				currenTags.removeAll(tags);
			}
			JSONArray r= new JSONArray();
			r.addAll(currenTags);
			Object l=json("add",json("doc",json(new String[]{"id",tagName},new Object[]{productId,json("set",r)})));
			log.info("Pushing tags to solr:{}",l);
			solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",l);
		}
	}

	private List<String> toLowerCase(List list){
		if(list==null){
			return null;
		}
		for(int i=0; i < list.size(); i++) {
			  list.set(i, list.get(i).toString().toLowerCase());
		}
		return list;
	}
	protected void tag(String productId, List<String>  tagsRequest, String tagName) throws MissingFieldException, NoSuchProductException {
		if(tagsRequest.size()==0  ){
			throw new MissingFieldException(tagName);
		}
		Set<String> tags=new HashSet<String>(toLowerCase(tagsRequest));
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		if(!existsProduct(productId, tenantId)){
			throw new NoSuchProductException("No such product");
		}
		String url=getSolrReadUrl()+"?wt=json&fl="+tagName+"&q=apiKey:"+tenantId+" AND id:"+productId;
		String rawResult=solrWriteRestTemplateProxy.getRestTemplate().getForObject(url, String.class);

		JSONObject jsonRawResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)((JSONObject)jsonRawResult.get("response")).get("docs");
		if(docs.size()!=0){
			List currenTags=toLowerCase((List)((JSONObject)docs.get(0)).get(tagName));
			if(currenTags!=null){
				tags.addAll(currenTags);
			}
		}
		JSONArray r= new JSONArray();
		r.addAll(tags);
		Object l=json("add",json("doc",json(new String[]{"id",tagName},new Object[]{productId,json("set",r)})));
		log.info("Pushing tags to solr:{}",l);
		solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",l);
	}

	private boolean existsProduct(String productId, String tenantId){
		String url=getSolrReadUrl()+"?wt=json&fl=id&q=apiKey:"+tenantId+" AND id:"+productId;
		String rawResult=solrReadRestTemplateProxy.getRestTemplate().getForObject(url, String.class);
		JSONObject jsonRawResult=(JSONObject)JSONValue.parse(rawResult);
		List docs=(List)((JSONObject)jsonRawResult.get("response")).get("docs");
		return docs.size()!=0;
	}

	private JSONObject json(String key,Object value){
		JSONObject json= new JSONObject();
		json.put(key, value);
		return json;
	}
	private JSONObject json(String keys[],Object values[]){
		JSONObject json= new JSONObject();
		for(int count=0; count<keys.length; count++){
			json.put(keys[count], values[count]);
		}
		return json;
	}
	@Override
	public JSONObject search(Integer pageSize, Integer pageNumber,	JSONObject query, String sortField, String sortOrder) {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject tenant=metadataProvider.getTenantById(tenantId);
		String queryTemplate=(String)tenant.get("queryTemplate");
		JSONObject result=search(tenantId,StringUtils.isEmpty(queryTemplate)?getDefaultQueryTemplate():queryTemplate, query,pageSize, pageNumber,sortField,sortOrder);
		return metadataProvider.filter(result);
	}


	@Override
	public void removeGenreFromProduct(String productId, List<String>  genres) throws MissingFieldException, NoSuchProductException {
		checkTagExists(genres, "genres");
		untag(productId, genres, "genres");
	}

	@Override
	public void removeOfferFromProduct(String productId, List<String>  offers) throws MissingFieldException, NoSuchProductException {
		checkTagExists(offers, "offers");
		untag(productId, offers, "offers");
	}

	@Override
	public void setAvgUserRating(JSONObject [] ratings) throws MissingFieldException, NoSuchProductException {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		for(JSONObject rating:ratings){
			String productId=(String)rating.get("productId");
			if(productId==null || rating.get("avgUserRating")==null || rating.get("avgUserRatingCount")==null){
				log.info("Not enough information for rating product:{}",rating);
				continue;
			}
			Object l=json("add",json("doc",json(new String[]{"id","avgUserRating","avgUserRatingCount"},new Object[]{(String)rating.get("productId"),json("set",(String)rating.get("avgUserRating")),json("set",(String)rating.get("avgUserRatingCount"))})));
			log.info("Pushing tags to solr:{}",l);
			solrWriteRestTemplateProxy.getRestTemplate().postForLocation(getSolrWriteUrl()+"?commit=true",l);
			updateSolrBlob(productId, tenantId);
		}
	}

	@Override
	public void addOfferToProduct(String productId, List<String>  offersRequest)throws MissingFieldException, NoSuchProductException {
		checkTagExists(offersRequest, "offers");
		tag(productId, offersRequest, "offers");
	}

	@Override
	public void addGenreToProduct(String productId, List<String>  genresRequest)	throws MissingFieldException, NoSuchProductException {
		checkTagExists(genresRequest, "genres");
		tag(productId, genresRequest, "genres");
	}

	@Override
	public void removeTagFromProduct(String productId, List<String>  tags) throws MissingFieldException, NoSuchProductException {
		untag(productId, tags, "tags");
	}

	@Override
	public void addTagToProduct(String productId, List<String>  tags) throws MissingFieldException, NoSuchProductException {
		tag(productId, tags, "tags");
	}


	private void  checkTagExists(List<String>  tags, String tagName){
		;
	}


	@Override
	public JSONObject getProductsByOfferId(String offerId, Integer pageSize, Integer pageNumber) {
		JSONObject credentials=CredentialsThreadLocal.getCredentials();
		String tenantId=(String)credentials.get("tenantId");
		JSONObject finalResult=search(" offerId:"+offerId, tenantId, pageSize, pageNumber,null,null);
		return metadataProvider.filter(finalResult);
	}

	@Override
	public void removeNamedTagFromProduct(String productId, List<String> tags,String name) throws MissingFieldException, NoSuchProductException {
		untag(productId, tags, name);

	}

	@Override
	public void addNamedTagToProduct(String productId, List<String>  tags,String name) throws MissingFieldException, NoSuchProductException {
		tag(productId, tags, name);

	}

}
