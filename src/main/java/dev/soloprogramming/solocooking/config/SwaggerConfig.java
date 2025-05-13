package dev.soloprogramming.solocooking.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                .pathsToMatch("/recipes/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .servers(List.of(server)))
                .build();
    }
}
