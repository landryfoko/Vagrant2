package com.libertas.vipaas.common.cloud.rest.api;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;




import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
//import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableOAuth2Sso
@ComponentScan("com.libertas.vipaas")
@Slf4j
public class RestApiConfiguration {//implements SmartLifecycle, Ordered, BeanPostProcessor {
	/*
    @Slf4j
public class ExtRibbonInterceptor extends RibbonInterceptor {


        private final LoadBalancerClient loadBalancer2;

        public ExtRibbonInterceptor(final LoadBalancerClient loadBalancer) {
            super(loadBalancer);
            loadBalancer2 = loadBalancer;
        }

        private String getIp(final ServiceInstance instance) {
            try {
                final Field field = instance.getClass().getDeclaredField("server");
                field.setAccessible(true);
                final DiscoveryEnabledServer server = (DiscoveryEnabledServer) field.get(instance);
                return server.getInstanceInfo().getIPAddr();
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                return null;
            }
        }

        @Override
        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
            final HttpRequestWrapper wrapper = new HttpRequestWrapper(request) {
                @Override
                public URI getURI() {
                    final URI originalUri = super.getURI();
                    final String serviceName = originalUri.getHost();
                    final ServiceInstance instance = loadBalancer2.choose(serviceName);
                    final String ip = getIp(instance);
                    final URI uri = UriComponentsBuilder.fromUri(originalUri)
                            .host(ip)
                            .port(instance.getPort())
                            .build(true)
                            .toUri();
                    log.info("Original {} => {}", originalUri, uri);
                    return uri;
                }
            };
            return execution.execute(wrapper, body);
        }
    }


    private boolean running;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
        public int getPhase() {
            return 0;
        }

    @Bean
    public EurekaInstanceConfigBean instanceConfig(final Environment env) {
        final EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean();

        final String virtualHostName = env.getProperty("eureka.instance.virtualHostName");
        final Integer serverPort = env.getProperty("server.port", Integer.class);

        log.info("virtualHostName {}:{}", virtualHostName, serverPort);

        bean.setDataCenterInfo(new DataCenterInfoImpl(virtualHostName, serverPort));

        bean.setHostname(virtualHostName);

        return bean;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if ("ribbonInterceptor".equals(beanName))
            return applicationContext.getBean("ribbonInterceptor2");
        else
            return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Bean
    public RibbonInterceptor ribbonInterceptor2(final LoadBalancerClient loadBalancerClient) {
        return new ExtRibbonInterceptor(loadBalancerClient);
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void stop(final Runnable callback) {
        stop();
    }*/
}
