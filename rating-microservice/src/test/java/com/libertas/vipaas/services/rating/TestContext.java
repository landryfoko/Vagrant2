package com.libertas.vipaas.services.rating;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public RatingService ratingService() {
        return Mockito.mock(RatingService.class);
    }
    
    @Bean
    public RatingController ratingController() {
        return new RatingController();
    }
    
}
