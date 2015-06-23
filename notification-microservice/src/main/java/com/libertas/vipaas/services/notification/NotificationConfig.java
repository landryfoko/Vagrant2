package com.libertas.vipaas.services.notification;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class NotificationConfig {

	@Bean
	public VelocityEngine getVelocityEngine(){
		VelocityEngine ve = new VelocityEngine();
        ve.init();
        return ve;
	}
}
