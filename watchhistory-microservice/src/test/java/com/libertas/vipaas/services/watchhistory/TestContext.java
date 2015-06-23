package com.libertas.vipaas.services.watchhistory;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public WatchHistoryService watchHistoryService() {
        return Mockito.mock(WatchHistoryService.class);
    }
    
    @Bean
    public WatchHistoryController watchHistoryController() {
        return new WatchHistoryController();
    }
    
}
