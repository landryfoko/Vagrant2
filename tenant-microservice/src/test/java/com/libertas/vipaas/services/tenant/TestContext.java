package com.libertas.vipaas.services.tenant;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public TenantService tenantService() {
        return Mockito.mock(TenantService.class);
    }
    
    @Bean
    public TenantController tenantController() {
        return new TenantController();
    }
    
}
