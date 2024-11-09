package com.sparta.jwt.infrastructure.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version : 1.0.0
 * @package : com.sparta.jwt.infrastructure.configuration
 * @name : OpenApiConfig.java
 * @date : 2024-11-10 오전 7시
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    private static final String API_NAME = "Jwt";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "✨Backend Onboarding Assignment API✨";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION));
    }
}