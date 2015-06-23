package com.libertas.vipaas.services.ingestion;

import org.springframework.boot.SpringApplication;

import com.libertas.vipaas.common.hystrix.LibertasHystrixConcurrencyStrategy;

public class IngestionServiceLauncher {
    public static void main(final String[] args) throws Exception {
    	SpringApplication.run(RestApiConfig.class, args);
    }
}

