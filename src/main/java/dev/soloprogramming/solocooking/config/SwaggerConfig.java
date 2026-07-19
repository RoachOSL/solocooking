/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.config;

import java.util.List;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    @Bean
    GroupedOpenApi SoloProgrammingApi() {
        var server = new Server();
        server.setUrl("/api");
        server.setDescription("Base API path for SoloCooking app");

        return GroupedOpenApi.builder()
                .group("soloprogramming")
                .displayName("SoloCooking")
                .pathsToMatch("/recipes/**", "/ingredients/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("SoloCooking API")
                                .version("v1"))
                        .servers(List.of(server)))
                .build();
    }
}
