package com.libertas.vipaas.services.notification;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.Getter;
import lombok.Setter;
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
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.common.json.JSONHelper;
import com.libertas.vipaas.common.messaging.ProducerTemplate;
import com.libertas.vipaas.common.messaging.kafka.KafkaConsumer;
import com.libertas.vipaas.common.metadata.MetadataProvider;
import com.libertas.vipaas.common.servlet.CorsFilter;

@Service
@ConfigurationProperties("notification")
@Slf4j
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private MetadataProvider metadataProvider;

	@Autowired
	private VelocityEngine velocityEngine;



	private Map<String,String> smtpProperties;
	private Map<String,String> velocityTemplates;
	@Autowired @Getter @Setter
	private RestTemplateProxy restTemplateProxy;

	//@Autowired
	private ProducerTemplate producer;
	private String databaseServiceName;
	@Getter @Setter
	private String standardHotfolderBaseDirectory;

	@Getter @Setter
	private String edgecastHotfolderBaseDirectory;
	@Getter @Setter
	private String uplynkHotfolderBaseDirectory;

	public Map<String, String> getVelocityTemplates() {
		return velocityTemplates;
	}

	public void setVelocityTemplates(Map<String, String> velocityTemplates) {
		this.velocityTemplates = velocityTemplates;
	}

	public Map<String, String> getSmtpProperties() {
		return smtpProperties;
	}

	public void setSmtpProperties(Map<String, String> smtpProperties) {
		this.smtpProperties = smtpProperties;
	}

	public String getDatabaseServiceName() {
		return databaseServiceName;
	}

	public void setDatabaseServiceName(String databaseServiceName) {
		this.databaseServiceName = databaseServiceName;
	}

	public JSONObject getTenantById(String tenantId) throws NoSuchUserException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> tenant=getRestTemplateProxy().getRestTemplate().exchange(getDatabaseServiceName()+"/tenant/"+tenantId+"?apiKey="+tenantId,HttpMethod.GET,entity, JSONObject.class);
		if(tenant==null || tenant.getBody()==null){
			throw new NoSuchUserException("No user with given Id");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(tenant.getBody());
		if(unmarshalled!=null && unmarshalled.size()>0 ){
			return unmarshalled;
		};
		throw new NoSuchUserException("No user with given Id");
	}

	public JSONObject getCustomerById(String customerId) throws NoSuchUserException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept","application/json");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> customer=getRestTemplateProxy().getRestTemplate().exchange(getDatabaseServiceName()+"/customer/"+customerId+"?customerId="+customerId,HttpMethod.GET,entity, JSONObject.class);
		if(customer==null || customer.getBody()==null){
			throw new NoSuchUserException("No user with given Id");
		}
		JSONObject unmarshalled=JSONHelper.unmarshall(customer.getBody());
		if(unmarshalled!=null && unmarshalled.size()>0 ){
			return unmarshalled;
		};
		throw new NoSuchUserException("No user with given Id");
	}

	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{customer.createCustomerTopic}" }, groupId="#{customer.createCustomerTopic}")
	public void notifyCreateCustomer(KafkaStream kafkaStream,ConsumerConnector consumer, byte[] message) {
		String eventType="createCustomer";
		log.info("Processing event {} with notification type ",new String(message),eventType);
		JSONObject event= (JSONObject)JSONValue.parse(new String(message));
		notify(event,eventType);
	}

	public void notify(JSONObject event, String eventType) {
		String tenantId=(String)event.get("tenantId");
		String customerId=(String)event.get("customerId");
		JSONObject tenant;
		try {
			tenant = getTenantById(tenantId);
		} catch (NoSuchUserException e) {
			log.error(e.getMessage(),e);
			return;
		}
		if(tenant.get("notificationEnabled")==null || !(Boolean)tenant.get("notificationEnabled")){
			log.info("Notification not enabled for tenant:{}. Skipping notification for event:{}",tenantId,eventType);
			return;
		}
		JSONObject customer;
		try {
			customer = getCustomerById(customerId);
			customer.remove("password");
			event.putAll(customer);
		} catch (NoSuchUserException e) {
			log.error(e.getMessage(),e);
			return;
		}
		VelocityContext context= new VelocityContext();
		context.put("newline", "\n");
		for(Object key:event.keySet()){
			context.put(key.toString(), event.get(key));
		}
		StringWriter writer= new StringWriter();

		Map<String,String> templates=(Map<String,String>)tenant.get("velocityTemplates");
		templates= templates==null?velocityTemplates:templates;
		String body=((String)templates.get(eventType+"NotificationTemplate")).replace("\\$", "$");
		String subject=(String)templates.get(eventType+"NotificationSubject").replace("\\$", "$");
		context.get("firstName");
		context.get("serviceName");
		velocityEngine.evaluate(context, writer,eventType , body);
		body=writer.toString();
		writer=new StringWriter();
		velocityEngine.evaluate(context, writer,eventType , subject);
		subject=writer.toString();
		log.info("Velocity processed subject:{}",writer);
		try {
			Properties props= new Properties();
			props.putAll(tenant.get("smtpProperties")==null?getSmtpProperties():(JSONObject)tenant.get("smtpProperties"));
			Session session=getSmtpSession(tenantId, tenant, props);
			Message mail = new MimeMessage(session);
			mail.setFrom(new InternetAddress((String)props.get("mail.from")));
			mail.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse((String)event.get("email")));
			mail.setSubject(subject);
			mail.setText(body);
			Transport.send(mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}


	public Session getSmtpSession(final String tenantId,final  JSONObject tenant,final  Properties props){
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication((String)props.getProperty("mail.username"), (String)props.getProperty("mail.password"));
					}
				  });
		 return session;
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{customer.updateCustomerTopic}" }, groupId="#{customer.updateCustomerTopic}")
	public void notifyUpdateCustomer(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		JSONObject event= (JSONObject)JSONValue.parse(new String(message));
		String eventType="updateCustomer";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,event);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{customer.deleteCustomerTopic}" }, groupId="#{customer.deleteCustomerTopic}")
	public void notifyDeleteCustomer(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		JSONObject event= (JSONObject)JSONValue.parse(new String(message));
		String eventType="deleteCustomer";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,event);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{customer.changePasswordTopic}" }, groupId="#{customer.changePasswordTopic}")
	public void notifyChangePassword(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="changePassword";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{customer.resetPasswordTopic}" }, groupId="#{customer.resetPasswordTopic}")
	public void notifyResetPassword(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="resetPassword";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{purchase.purchaseTopic}" }, groupId="#{purchase.purchaseTopic}")
	public void notifyPurchase(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="purchase";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{tenant.createTenantTopic}" }, groupId="#{tenant.createTenantTopic}")
	public void notifyCreateTenant(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) throws NoSuchUserException {
		String eventType="createTenant";
		log.info("Processing event {} with notification type ",new String(message),eventType);
		JSONObject event= (JSONObject)JSONValue.parse(new String(message));
		String tenantId=(String)event.get("id");
		event.put("tenantId", tenantId);
		event.put("customerId", event.get("ownerId"));
		notify(event,eventType);
		log.info("Sent {} notification:{}",eventType,event);
		JSONObject tenant=getTenantById(tenantId);
		File f= new File(getStandardHotfolderBaseDirectory()+"/"+tenantId);
		boolean created=false;
		if(tenant.get("createStandardFileSystemHotfolder")==null || tenant.get("createStandardFileSystemHotfolder").toString().equalsIgnoreCase("true")){
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
		}
		if(tenant.get("createEdgecastFileSystemHotfolder")==null || tenant.get("createEdgecastFileSystemHotfolder").toString().equalsIgnoreCase("true")){
			f= new File(getEdgecastHotfolderBaseDirectory()+"/"+tenantId+"/hls/video");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
			f= new File(getEdgecastHotfolderBaseDirectory()+"/"+tenantId+"/hls/preview");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
			f= new File(getEdgecastHotfolderBaseDirectory()+"/"+tenantId+"/mp4/preview");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
			f= new File(getEdgecastHotfolderBaseDirectory()+"/"+tenantId+"/mp4/video");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
		}
		if(tenant.get("createUplynkFileSystemHotfolder")==null || tenant.get("createUplynkFileSystemHotfolder").toString().equalsIgnoreCase("true")){
			f= new File(getUplynkHotfolderBaseDirectory()+"/"+tenantId+"/video");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
			f= new File(getUplynkHotfolderBaseDirectory()+"/"+tenantId+"/preview");
			created=f.mkdirs();
			if(created){
				log.info("Hotfolder {} created ",f.getAbsolutePath());
			}else{
				log.info("Could NOT create hotfolder {} ",f.getAbsolutePath());
			}
		}
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{tenant.updateTenantTopic}" }, groupId="#{tenant.updateTenantTopic}")
	public void notifyUpdateTenant(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="updateTenant";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{tenant.notifyTenantTopic}" }, groupId="#{tenant.notifyTenantTopic}")
	public void notifyDeleteTenant(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="deleteTenant";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{tenant.addAdministratorTopic}" }, groupId="#{tenant.addAdministratorTopic}")
	public void notifyAddAdministrator(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="addAdministrator";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}


	@KafkaConsumer(configRef = "defaultKafkaConfig", topics = { "#{tenant.removeAdministratorTopic}" }, groupId="#{tenant.removeAdministratorTopic}")
	public void notifyRemoveAdministrator(KafkaStream kafkaStream,
			ConsumerConnector consumer, byte[] message) {
		String eventType="removeAdministrator";
		//notify(event,eventType);
		log.info("Sending {} notification:{}",eventType,eventType);
	}

}
