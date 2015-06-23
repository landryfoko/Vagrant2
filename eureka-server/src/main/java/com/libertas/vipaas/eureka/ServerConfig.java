package com.libertas.vipaas.eureka;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableEurekaServer
public class ServerConfig {
    @Bean
    public EurekaInstanceConfigBean instanceConfig(final Environment env) {
        final EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean();

        bean.setDataCenterInfo(new DataCenterInfoImpl(env.getProperty("eureka.instance.virtualHostName"), env.getProperty("server.port", Integer.class)));

        bean.setHostname(env.getProperty("eureka.instance.virtualHostName"));

        return bean;
    }
}
