package com.libertas.vipaas.services.bookmark;

import org.springframework.boot.SpringApplication;

import com.libertas.vipaas.common.hystrix.LibertasHystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.HystrixPlugins;

public class BookmarkServiceLauncher {
    public static void main(final String[] args) throws Exception {
		HystrixPlugins.getInstance().registerConcurrencyStrategy(new LibertasHystrixConcurrencyStrategy());
    	SpringApplication.run(RestApiConfig.class, args);
    }
}

