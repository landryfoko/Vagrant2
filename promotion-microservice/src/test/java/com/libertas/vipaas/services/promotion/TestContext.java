package com.libertas.vipaas.services.promotion;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public PromotionService promotionService() {
        return Mockito.mock(PromotionService.class);
    }
    
    @Bean
    public PromotionAdminController promotionAdminController() {
        return new PromotionAdminController();
    }
    
    @Bean
    public PromotionController promotionController() {
        return new PromotionController();
    }
}
