package com.libertas.vipaas.common.cloud.rest.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.libertas.vipaas.common.configs.CacheConfig;
import com.libertas.vipaas.common.configs.ServletFilterConfiguration;
import com.libertas.vipaas.common.configs.SwaggerConfig;
import com.libertas.vipaas.common.messaging.kafka.KafkaProducerConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RestApiConfiguration.class,ServletFilterConfiguration.class,  KafkaProducerConfig.class, CacheConfig.class})
public @interface EnableRestApi {

}
