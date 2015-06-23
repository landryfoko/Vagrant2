package com.libertas.vipaas.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component

public class RestTemplateProxy {

	
	private @Getter @Setter RestTemplate restTemplate;
	@Autowired
//	public RestTemplateProxy(RibbonInterceptor ribbonInterceptor){
//		restTemplate = new RestTemplate();
//		restTemplate.setInterceptors(Arrays.asList(ribbonInterceptor));
//	}
	public RestTemplateProxy(LoadBalancerInterceptor loadBalancerInterceptor){
		restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> list = new ArrayList<>();
		list.add(loadBalancerInterceptor);
		restTemplate.setInterceptors(list);
	}
	

}
