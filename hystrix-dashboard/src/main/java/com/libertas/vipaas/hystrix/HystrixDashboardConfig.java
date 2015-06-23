package com.libertas.vipaas.hystrix;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;


@EnableConfigurationProperties
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableHystrixDashboard
public class HystrixDashboardConfig {
	@Bean
	public String test(final Environment env) {
		final String property = env.getProperty("miguel.test");

		System.err.println(property);

		return property;
	}


    @Bean
    @Lazy
    public ServiceInstance turbine(final LoadBalancerClient loadBalancerClient) throws InterruptedException {
        final ServiceInstance serviceInstance = loadBalancerClient.choose("klaus-rest-api");

        System.err.println(serviceInstance);

        return serviceInstance;
    }
}
