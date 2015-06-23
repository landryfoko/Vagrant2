package com.libertas.vipaas.services.creditcard;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
	    
    @Bean
    public CreditCardService creditCardService() {
        return Mockito.mock(CreditCardService.class);
    }
    
    @Bean
    public CreditCardController creditCardController() {
        return new CreditCardController();
    }
    
}
