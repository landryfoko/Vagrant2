package com.libertas.vipaas.oauth;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import java.util.List;
@Configuration
@EnableAuthorizationServer
@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
@Slf4j
@ConfigurationProperties("oauth2")
public  class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	@Getter @Setter private String privateKey;
	@Getter @Setter private String publicKey;
	@Getter @Setter private String clientId;
	@Getter @Setter private String secret;
	@Getter @Setter private List<String> resources;
	@Getter @Setter private List<String> grantTypes;
	@Getter @Setter private List<String> scopes;
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		log.info("Starting Jwt with public Key:{}",getPublicKey());
		converter.setVerifierKey(getPublicKey());
		converter.setSigningKey(getPrivateKey());
		return converter;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		InMemoryClientDetailsServiceBuilder inMemory=clients.inMemory();
		inMemory.withClient(getClientId())
				.secret(getSecret())
				.resourceIds(getResources().toArray(new String[]{}))
				.authorizedGrantTypes(getGrantTypes().toArray(new String[]{})).scopes(getScopes().toArray(new String[]{}));
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter());
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer)
			throws Exception {
		oauthServer.tokenKeyAccess("permitAll()");
	}

}