package com.libertas.vipaas.common.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class CredentialsInjectionFilter implements Filter {
    @Autowired @Getter @Setter
    private Environment environment;

	@Override
    public void destroy() { }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        SecurityContext securityContext = SecurityContextHolder.getContext(); 
        Authentication authentication = securityContext.getAuthentication();
        String apiKey=null;
        if(!httpServletRequest.getRequestURI().startsWith("/api-docs") && !httpServletRequest.getRequestURI().toLowerCase().startsWith("/cors")){
	        if(authentication!=null && authentication.getAuthorities().size()==0){
	        	log.error("Token does not contain required grant. Token details:{}",authentication);
	        	throw new BadCredentialsException("Not enough authorities");
	        }
	        if(authentication!=null){
	        	JSONObject principal=(JSONObject)org.json.simple.JSONValue.parse((String)authentication.getPrincipal());
	        	apiKey=(String)principal.get("apiKey");
	        	 CredentialsThreadLocal.setCredentials(principal);
	        	 log.info("Credentials found in context with principal:{}",principal);
	        }else{
	        	//For cases that require no authentication, we need to carry the apiKey along
	            CredentialsThreadLocal.setCredentials(new JSONObject());
	        }
	        if(httpServletRequest.getRequestURI().startsWith("/internal")){
	        	 String customerId = httpServletRequest.getParameter("customerId");
	        	 if(!StringUtils.isEmpty(customerId)){
	        		 CredentialsThreadLocal.getCredentials().put("customerId", customerId);
	        	 }
	        }
	        if(StringUtils.isEmpty(apiKey)){
	        	   apiKey = httpServletRequest.getParameter("apiKey");
	        	     
	        }
	        if(StringUtils.isEmpty(apiKey)){
	     	   apiKey = httpServletRequest.getHeader("apiKey");
	        }
	        if(StringUtils.isNotEmpty(apiKey)){
	        	 CredentialsThreadLocal.getCredentials().put("tenantId", apiKey);
	        	 CredentialsThreadLocal.getCredentials().put("apiKey", apiKey);
	         }
	        String apiKeyValidationEnabled=getEnvironment().getProperty("general.apiKeyValidationEnabled");
	        if(StringUtils.isEmpty(apiKeyValidationEnabled) || apiKeyValidationEnabled.equalsIgnoreCase("true")){
		        if (StringUtils.isEmpty(apiKey) && !(httpServletRequest.getMethod().equals("POST") && httpServletRequest.getRequestURI().endsWith("tenant"))){
		           throw new IllegalArgumentException("Missing apiKey in request:" + httpServletRequest.getMethod() + ":" + httpServletRequest.getRequestURL());
		        };
	        }
        }
        filterChain.doFilter(httpServletRequest, servletResponse);
    }


    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

}