package ru.langgame.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Конфигурация OpenAPI/Swagger с поддержкой JWT (кнопка Authorize). */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";

    @Bean
    public OpenAPI langGameOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("«Сёз оюн» - API обучения карачаевскому языку")
                        .version("0.1.0")
                        .description("REST API: аутентификация (JWT), словарь, игры, лидерборд."))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
