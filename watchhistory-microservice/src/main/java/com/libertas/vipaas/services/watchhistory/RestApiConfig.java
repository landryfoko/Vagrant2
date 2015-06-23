package com.libertas.vipaas.services.watchhistory;

//import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import com.libertas.vipaas.common.cloud.rest.api.EnableRestApi;

@Configuration
@EnableRestApi
//@EnableOAuth2Resource
public class RestApiConfig{ /*extends AuthorizationServerConfigurerAdapter {
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("hasAuthority('USER')");
    }*/
}
