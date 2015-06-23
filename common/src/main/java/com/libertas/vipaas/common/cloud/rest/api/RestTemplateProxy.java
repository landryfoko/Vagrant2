package com.libertas.vipaas.common.cloud.rest.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
//import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

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

//class RibbonLoadBalancerInterceptor{
//	@ConditionalOnMissingBean(RestTemplate.class)
//	public RestTemplate restTemplate(LoadBalancerInterceptor loadBalancerInterceptor) {
//		RestTemplate restTemplate = new RestTemplate();
//		List<ClientHttpRequestInterceptor> list = new ArrayList<>();
//		list.add(loadBalancerInterceptor);
//		restTemplate.setInterceptors(list);
//		return restTemplate;
//	}
//
//	@Bean
//	public LoadBalancerInterceptor ribbonInterceptor(LoadBalancerClient loadBalancerClient) {
//		return new LoadBalancerInterceptor(loadBalancerClient);
//	}
//}
