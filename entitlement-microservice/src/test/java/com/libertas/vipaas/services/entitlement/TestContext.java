package com.libertas.vipaas.services.entitlement;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public EntitlementService entitlementService() {
        return Mockito.mock(EntitlementService.class);
    }
    
    @Bean
    public EntitlementAdminController entitlementAdminController() {
        return new EntitlementAdminController();
    }
    
    @Bean
    public EntitlementController entitlementController() {
        return new EntitlementController();
    }
    
}
