package com.libertas.vipaas.services.customer;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public CustomerService customerService() {
        return Mockito.mock(CustomerService.class);
    }
    
    @Bean
    public CustomerController customerController() {
        return new CustomerController();
    }
    
}
