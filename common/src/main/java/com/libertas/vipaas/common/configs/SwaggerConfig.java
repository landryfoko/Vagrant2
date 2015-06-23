package com.libertas.vipaas.common.configs;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableSwagger
@EnableWebMvc
@ComponentScan("com.libertas")
public class SwaggerConfig {
	private SpringSwaggerConfig springSwaggerConfig;
	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}
	@Bean(name="mvcPluginAdmin")
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig) .swaggerGroup("users").apiInfo(
				userApiInfo()).includePatterns("/v1/(?!admin).*");
	}
	private ApiInfo userApiInfo() {
		ApiInfo apiInfo = new ApiInfo("Customer API", "API for Libertas ViPAaaS",
				"Libertas API terms of service", "pascal@libertas.net",
				"Libertas API Licence Type", "Libertas API License URL");
		return apiInfo;
	}
	
	@Bean(name="mvcPluginUsers")
	public SwaggerSpringMvcPlugin adminMvcPlugin() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig) .swaggerGroup("admin").apiInfo(
				adminApiInfo()).includePatterns("/v1/admin/.*");
	}
	private ApiInfo adminApiInfo() {
		ApiInfo apiInfo = new ApiInfo("Customer API", "API for Libertas ViPAaaS Admin Interface",
				"Libertas Admin API terms of service", "pascal@libertas.net",
				"Libertas Admin API Licence Type", "Libertas Admin API License URL");
		return apiInfo;
	}
}