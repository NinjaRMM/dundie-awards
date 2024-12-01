package com.ninjaone.dundie_awards;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Dundie Awards API",
		description = "API to provide data management to Dundie Awards App", version = "v1"))
@Configuration
public class OpenApiConfig {
	
}
