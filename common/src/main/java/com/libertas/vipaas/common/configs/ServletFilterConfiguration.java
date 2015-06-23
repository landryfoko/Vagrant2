package com.libertas.vipaas.common.configs;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.libertas.vipaas.common.hystrix.HystrixRequestContextServletFilter;
import com.libertas.vipaas.common.servlet.CredentialsInjectionFilter;
import com.libertas.vipaas.common.servlet.LoggingFilter;

@Configuration
public class ServletFilterConfiguration extends RepositoryRestMvcConfiguration{

	@Bean
	public Filter getApiKeyFilter(){
		return new CredentialsInjectionFilter();
	}
	
	@Bean
	public Filter getHystrixContextFilter(){
		return new HystrixRequestContextServletFilter();
	}
	@Bean
	public Filter getLoggingFilter(){
		return new LoggingFilter();
	}
}
