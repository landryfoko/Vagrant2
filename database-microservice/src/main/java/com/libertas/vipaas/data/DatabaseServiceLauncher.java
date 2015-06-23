package com.libertas.vipaas.data;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;




@Configuration
@EnableMongoRepositories
public class DatabaseServiceLauncher {

	public static void main(String[] args) {
    	SpringApplication.run(RestApiConfiguration.class, args);
	}
}