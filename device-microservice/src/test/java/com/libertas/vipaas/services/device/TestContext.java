package com.libertas.vipaas.services.device;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public DeviceService deviceService() {
        return Mockito.mock(DeviceService.class);
    }
    
    @Bean
    public DeviceController deviceController() {
        return new DeviceController();
    }
    
}
