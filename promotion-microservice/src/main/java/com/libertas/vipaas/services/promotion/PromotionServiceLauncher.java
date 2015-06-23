package com.libertas.vipaas.services.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

import com.libertas.vipaas.common.hystrix.LibertasHystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.HystrixPlugins;



public class PromotionServiceLauncher {
    public static void main(final String[] args) throws Exception {
		HystrixPlugins.getInstance().registerConcurrencyStrategy(new LibertasHystrixConcurrencyStrategy());
    	SpringApplication.run(RestApiConfig.class, args);
    }
}

