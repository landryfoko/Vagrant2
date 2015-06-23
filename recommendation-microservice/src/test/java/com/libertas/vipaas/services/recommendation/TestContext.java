package com.libertas.vipaas.services.recommendation;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public RecommendationService offerService() {
        return Mockito.mock(RecommendationService.class);
    }
    
    @Bean
    public RecommendationAdminController recommendationAdminController() {
        return new RecommendationAdminController();
    }
    
    @Bean
    public RecommendationController recommendationController() {
        return new RecommendationController();
    }
    
}
