package com.libertas.vipaas.services.bookmark;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.libertas.vipaas.services.bookmark.BookmarkController;
import com.libertas.vipaas.services.bookmark.BookmarkService;

@Configuration
public class TestContext {
	    
    @Bean
    public BookmarkService bookmarkService() {
        return Mockito.mock(BookmarkService.class);
    }
    
    @Bean
    public BookmarkController bookmarkController() {
        return new BookmarkController();
    }
    
}
