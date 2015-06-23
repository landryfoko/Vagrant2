package com.libertas.vipaas.services.hls;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public HLSProviderService hlsProviderService() {
        return Mockito.mock(HLSProviderService.class);
    }
    
    @Bean
    public HLSProviderController hlsProviderController() {
        return new HLSProviderController();
    }
    
}
