package com.libertas.vipaas.hystrix;

import org.springframework.boot.SpringApplication;


public class HystrixDashboardLauncher {
    public static void main(final String[] args) throws InterruptedException {
        SpringApplication.run(HystrixDashboardConfig.class, args);
	}
}
