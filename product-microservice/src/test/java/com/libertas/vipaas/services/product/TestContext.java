package com.libertas.vipaas.services.product;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }
    
    @Bean
    public ProductAdminController productAdminController() {
        return new ProductAdminController();
    }
    
    @Bean
    public ProductController productController() {
        return new ProductController();
    }
    
}
