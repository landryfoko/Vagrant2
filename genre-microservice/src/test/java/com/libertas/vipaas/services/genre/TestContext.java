package com.libertas.vipaas.services.genre;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public GenreService genreService() {
        return Mockito.mock(GenreService.class);
    }
    
    @Bean
    public GenreAdminController genreAdminController() {
        return new GenreAdminController();
    }
    
    @Bean
    public GenreController genreController() {
        return new GenreController();
    }
    
}
