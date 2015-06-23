package com.libertas.vipaas.services.playback;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.libertas.vipaas.services.notification.NotificationController;
import com.libertas.vipaas.services.notification.NotificationService;

@Configuration
public class TestContext {
	    
    @Bean
    public NotificationService playbackService() {
        return Mockito.mock(NotificationService.class);
    }
    
    @Bean
    public NotificationController playbackController() {
        return new NotificationController();
    }
    
}
