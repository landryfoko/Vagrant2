package com.libertas.vipaas.oauth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ConfigurationProperties("oauth2")
public class CustomerAuthenticationProvider implements AuthenticationProvider {

	@Autowired @Getter @Setter
	private RestTemplateProxy restTemplateProxy;

	@Getter @Setter
    private String customerServiceName = "customer";
	@Getter @Setter
	private String clientId="libertas";

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String password = (String) authentication.getCredentials();
        final String principal = (String) authentication.getPrincipal();
        JSONObject request = new JSONObject();
        if(principal.equals(clientId)){
        	log.info("Client {} authenticated",clientId);
            request.put("principal", principal);
        	return  new UsernamePasswordAuthenticationToken(clientId, password,	new ArrayList<SimpleGrantedAuthority>());
        }

        if(principal.indexOf("/")<0){
        	log.info("Invalid principal in credentials:{}",authentication.getPrincipal());
        	throw new BadCredentialsException("Missing tenantId in username. Should be in the form email/apiKey.");
        }
        String parts []=principal.split("/");
        request.put("email", parts[0]);
        request.put("password", password);
        request.put("apiKey", parts[1]);

        List<String> grants=new ArrayList<String>();
        grants.add("USER");
        try{
            final ResponseEntity<JSONObject> responseEntity = restTemplateProxy.getRestTemplate().postForEntity(String.format("http://%s/internal/v1/customer/login?apiKey=%s", getCustomerServiceName(), parts[1]), request, JSONObject.class);
            String customerId=(String)responseEntity.getBody().get("id");
            request.put("customerId", customerId);
        } catch (final Exception e) {
        	log.error(e.getMessage(),e);
            throw new BadCredentialsException("Could not authenticate user", e);
        }
        List<SimpleGrantedAuthority> authorities= new ArrayList<SimpleGrantedAuthority>();
        for(String grant:grants){
        	authorities.add(new SimpleGrantedAuthority(grant));
        }
        final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(request, password, authorities);
       return result;
    }
    @Override
    public boolean supports(final Class<?> authentication) {
        return true;
    }
}
