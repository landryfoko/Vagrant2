package com.libertas.vipaas.services.ingestion;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import com.libertas.vipaas.common.cloud.rest.api.EnableRestApi;

@EnableAutoConfiguration
@Configuration
@EnableRestApi
public class RestApiConfig {

}
