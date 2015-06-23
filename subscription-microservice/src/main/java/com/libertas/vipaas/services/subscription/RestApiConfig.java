package com.libertas.vipaas.services.subscription;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.netflix.appinfo.DataCenterInfo;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableDiscoveryClient
public class RestApiConfig {
    @Bean
    public EurekaInstanceConfigBean instanceConfig(final Environment env) {
        final EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean();
        bean.setDataCenterInfo(new DataCenterInfo() {
			@Override
			public Name getName() {
				return Name.MyOwn;
			}
		});
        bean.setHostname(env.getProperty("eureka.instance.virtualHostName"));
        return bean;
    }
}
