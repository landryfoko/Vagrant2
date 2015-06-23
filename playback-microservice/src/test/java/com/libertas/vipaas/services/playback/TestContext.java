package com.libertas.vipaas.services.playback;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public PlaybackService playbackService() {
        return Mockito.mock(PlaybackService.class);
    }
    
    @Bean
    public PlaybackController playbackController() {
        return new PlaybackController();
    }
    
}
