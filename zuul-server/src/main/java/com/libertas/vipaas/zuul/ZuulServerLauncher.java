package com.libertas.vipaas.zuul;

import org.springframework.boot.SpringApplication;


public class ZuulServerLauncher {
    public static void main(final String[] args) {
        SpringApplication.run(ServerConfig.class, args);
    }
}
