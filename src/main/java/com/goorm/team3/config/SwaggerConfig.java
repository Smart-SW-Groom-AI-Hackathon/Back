package com.goorm.team3.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Team3 API",
                description = "Goorm AI Hackathon Team3 API",
                version = "v1"))
@Configuration
public class SwaggerConfig {
}
