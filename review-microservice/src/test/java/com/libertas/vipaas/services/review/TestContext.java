package com.libertas.vipaas.services.review;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public ReviewService ratingService() {
        return Mockito.mock(ReviewService.class);
    }
    
    @Bean
    public ReviewController reviewController() {
        return new ReviewController();
    }
    
}
