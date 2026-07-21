/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.config;

import java.util.List;

import dev.soloprogramming.solocooking.common.exception.BadRequestProblemDetail;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

@Configuration
class SwaggerConfig {

    private static final String PROBLEM_DETAIL_SCHEMA = "#/components/schemas/ProblemDetail";
    private static final String BAD_REQUEST_PROBLEM_DETAIL_SCHEMA =
            "#/components/schemas/BadRequestProblemDetail";

    @Bean
    OpenAPI soloCookingOpenApi() {
        var server = new Server();
        server.setUrl("/api");
        server.setDescription("Base API path for SoloCooking app");

        var components = new Components();
        registerSchemas(components, ProblemDetail.class);
        registerSchemas(components, BadRequestProblemDetail.class);
        components.addResponses(
                "BadRequest",
                problemResponse("Request validation failed", BAD_REQUEST_PROBLEM_DETAIL_SCHEMA)
        );
        components.addResponses(
                "NotFound",
                problemResponse("Requested resource was not found", PROBLEM_DETAIL_SCHEMA)
        );
        components.addResponses(
                "Conflict",
                problemResponse("Request conflicts with current resource state", PROBLEM_DETAIL_SCHEMA)
        );

        return new OpenAPI()
                .info(new Info()
                        .title("SoloCooking API")
                        .version("v1"))
                .servers(List.of(server))
                .components(components);
    }

    @Bean
    GroupedOpenApi soloProgrammingApi() {
        return GroupedOpenApi.builder()
                .group("soloprogramming")
                .displayName("SoloCooking")
                .pathsToMatch("/recipes/**", "/ingredients/**")
                .build();
    }

    private void registerSchemas(Components components, Class<?> schemaType) {
        var resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(schemaType));
        resolvedSchema.referencedSchemas.forEach(components::addSchemas);
    }

    private ApiResponse problemResponse(String description, String schemaReference) {
        var schema = new Schema<>().$ref(schemaReference);
        var content = new Content();
        content.addMediaType(
                MediaType.APPLICATION_JSON_VALUE,
                new io.swagger.v3.oas.models.media.MediaType().schema(schema)
        );
        return new ApiResponse()
                .description(description)
                .content(content);
    }
}
