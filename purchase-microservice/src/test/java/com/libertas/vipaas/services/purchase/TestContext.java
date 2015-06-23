package com.libertas.vipaas.services.purchase;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public PurchaseService purchaseService() {
        return Mockito.mock(PurchaseService.class);
    }
    
    @Bean
    public PurchaseController purchaseController() {
        return new PurchaseController();
    }
    
}
