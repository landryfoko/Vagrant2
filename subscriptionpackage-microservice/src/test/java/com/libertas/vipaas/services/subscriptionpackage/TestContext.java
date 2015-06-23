package com.libertas.vipaas.services.subscriptionpackage;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public SubscriptionPackageService ratingService() {
        return Mockito.mock(SubscriptionPackageService.class);
    }
    
    @Bean
    public SubscriptionPackageController subscriptionPackageController() {
        return new SubscriptionPackageController();
    }
    
}
