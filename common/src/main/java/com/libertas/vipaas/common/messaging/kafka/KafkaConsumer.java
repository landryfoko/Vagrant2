package com.libertas.vipaas.common.messaging.kafka;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaConsumer {
	String [] topics();
	String configRef();
	String groupId() default "";
	int socketTimeoutMs() default 30000; 		
	int socketBuffersize() default 65536; 	
	int fetchSize() default 307200; 	 	
	int backoffIncrementMs() default 1000; 	 	
	int queuedchunksMax() default 100;
	boolean autocommitEnable() default true;
	int autocommitIntervalMs() default 10000;
	String autooffsetReset() default "smallest";
	int consumerTimeoutMs() default -1;
	int rebalanceRetriesMax() default 4; 		
	String mirrorTopicsWhitelist() default "";
	String mirrorTopicsBlacklist() default "";
	int mirrorConsumerNumthreads() default 4; 
} 