package com.libertas.vipaas.services.purchase;

import javax.servlet.Filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.libertas.vipaas.common.hystrix.HystrixRequestContextServletFilter;
import com.libertas.vipaas.common.servlet.LoggingFilter;
import com.libertas.vipaas.common.servlet.CredentialsInjectionFilter;

@Configuration
@ConfigurationProperties("purchase.braintree")
@Slf4j
public class BraintreeConfiguration extends RepositoryRestMvcConfiguration{
	private String environment="sandbox";
	private String publicKey;
	private String privateKey;
	private String merchantId;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

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

	@Bean
	public BraintreeGateway getBraintreeGateway(){
		BraintreeGateway gateway = new BraintreeGateway(getEnvironment().equalsIgnoreCase("sandbox")?Environment.SANDBOX:Environment.PRODUCTION,
				getMerchantId(),
				getPublicKey(),getPrivateKey());
		return gateway;

	}
}
