package com.libertas.vipaas.services.offer;

import kafka.javaapi.producer.Producer;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.libertas.vipaas.common.messaging.ProducerTemplate;

@Configuration
public class TestContext {
	    
    @Bean
    public OfferService offerService() {
        return Mockito.mock(OfferService.class);
    }
     
    @Bean
    public OfferAdminController offerAdminController() {
        return new OfferAdminController();
    }
    
}
