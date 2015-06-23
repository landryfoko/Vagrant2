package com.libertas.vipaas.common.messaging.kafka;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class KafkaProcessor implements ApplicationContextAware {

	final SecureRandom random= new SecureRandom();
	private ApplicationContext applicationContext;
	@Inject Environment e;

  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);
    for(final Object bean:beans.values()){
    	Method [] methods=bean.getClass().getMethods();
    	for(final Method method:methods){
    		KafkaConsumer annotation=method.getAnnotation(KafkaConsumer.class);
    		if(annotation!=null){
    			Map<String,String> m=new HashMap<String, String>();
    			if(StringUtils.isNotEmpty(annotation.configRef())){
    				m=(Map<String,String>)applicationContext.getBean(annotation.configRef());
    			}
    			Properties p= new Properties();
    			p.putAll(m);
    			String groupId=annotation.groupId();
    			if(StringUtils.isNotEmpty(groupId)){
    				groupId=groupId.replace("#random",Math.abs(random.nextLong())+"");
    				if(groupId.startsWith("#{") && groupId.endsWith("}")){
    					groupId=groupId.replace("#{", "").replace("}", "");
    					groupId=e.getProperty(groupId);
        				if(StringUtils.isEmpty(groupId)){
        					log.error("Could not find groupId {} defined in environment. Skiping it",annotation.groupId());
        					continue;
        				}
        			}
    				p.put("group.id",groupId);
    			}
    			ConsumerConfig c= new ConsumerConfig(p);
    	  		final ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(c);
        		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        		int count=0;
        		for(String topic:annotation.topics()){
        			String original=topic;
        			if(topic.startsWith("#{") && topic.endsWith("}")){
        				topic=topic.replace("#{", "").replace("}", "");
        				topic=e.getProperty(topic);
        				if(StringUtils.isEmpty(topic)){
        					log.error("Could not find topic {} defined in environment. Skiping it",original);
        					continue;
        				}
        			}
        			topic=topic.replace("#random",Math.abs(random.nextLong())+"");
        			log.info("Subscribing for events on topic:{}",topic);
        			topicCountMap.put(topic, new Integer(++count));
        		}
        		if(topicCountMap.size()==0){
        			log.error("No topic found for method:{}; Abandoning annotation.,method");
        			return;
        		}
        		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        		final KafkaStream<byte[], byte[]> stream = consumerMap.get(topicCountMap.keySet().iterator().next()).get(0);
        		final ConsumerIterator<byte[], byte[]> it = stream.iterator();
        		new Thread(){
        			public void run() {
        				while (it.hasNext()){
        					try {
								method.invoke(bean, stream,consumer,it.next().message());
							} catch (
									IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException e) {
								log.error(e.getMessage(),e);
							}
	            		}
        			};
        		}.start();
    		}
    	}
    }

  }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)	throws BeansException {
		this.applicationContext=applicationContext;
	}

}
