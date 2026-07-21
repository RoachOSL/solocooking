/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.config;

import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.API_SERVLET_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OpenApiContractIT extends BaseIntegrationTest {

    private static final String OPEN_API_ENDPOINT = API_SERVLET_PATH + "/v3/api-docs/soloprogramming";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExposeStableOpenApiContract() throws Exception {
        // when
        var response = mockMvc.perform(get(OPEN_API_ENDPOINT).servletPath(API_SERVLET_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        var openApi = objectMapper.readTree(response);

        // then
        assertThat(openApi.at("/info/title").stringValue()).isEqualTo("SoloCooking API");
        assertThat(openApi.at("/info/version").stringValue()).isEqualTo("v1");

        assertOperation(openApi, "/paths/~1recipes/post", "createRecipe", "201");
        assertProblemDetailResponse(openApi, "/paths/~1recipes/post", "400", "BadRequest");
        assertProblemDetailResponse(openApi, "/paths/~1recipes/post", "404", "NotFound");
        assertOperation(openApi, "/paths/~1recipes/get", "getRecipes", "200");
        assertOperation(openApi, "/paths/~1recipes~1{recipeId}/get", "getRecipe", "200");
        assertProblemDetailResponse(openApi, "/paths/~1recipes~1{recipeId}/get", "404", "NotFound");
        assertOperation(openApi, "/paths/~1recipes~1{recipeId}/put", "updateRecipe", "200");
        assertProblemDetailResponse(openApi, "/paths/~1recipes~1{recipeId}/put", "400", "BadRequest");
        assertProblemDetailResponse(openApi, "/paths/~1recipes~1{recipeId}/put", "404", "NotFound");
        assertThat(openApi.at("/paths/~1recipes~1{recipeId}/put/requestBody/content/application~1json").isMissingNode())
                .isFalse();
        assertThat(openApi.at("/paths/~1recipes~1{recipeId}/delete/operationId").stringValue())
                .isEqualTo("deleteRecipe");
        assertOperation(openApi, "/paths/~1ingredients/post", "createIngredient", "201");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients/post", "400", "BadRequest");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients/post", "409", "Conflict");
        assertOperation(openApi, "/paths/~1ingredients/get", "getIngredients", "200");
        assertOperation(openApi, "/paths/~1ingredients~1search/get", "searchIngredients", "200");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1search/get", "400", "BadRequest");
        assertQueryParameter(openApi, "/paths/~1ingredients~1search/get", "name");
        assertOperation(openApi, "/paths/~1ingredients~1{ingredientId}/get", "getIngredient", "200");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1{ingredientId}/get", "404", "NotFound");
        assertOperation(openApi, "/paths/~1ingredients~1{ingredientId}/patch", "updateIngredient", "200");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1{ingredientId}/patch", "400", "BadRequest");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1{ingredientId}/patch", "404", "NotFound");
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1{ingredientId}/patch", "409", "Conflict");
        assertThat(openApi.at("/paths/~1ingredients~1{ingredientId}/delete/operationId").stringValue())
                .isEqualTo("deleteIngredient");
        assertThat(openApi.at("/paths/~1ingredients~1{ingredientId}/delete/responses/204").isMissingNode())
                .isFalse();
        assertProblemDetailResponse(openApi, "/paths/~1ingredients~1{ingredientId}/delete", "409", "Conflict");

        assertProblemDetailComponent(openApi, "BadRequest", "BadRequestProblemDetail");
        assertProblemDetailComponent(openApi, "NotFound", "ProblemDetail");
        assertProblemDetailComponent(openApi, "Conflict", "ProblemDetail");

        var schemas = openApi.at("/components/schemas");
        assertRequired(schemas, "RecipeDTO", "id", "name", "imageUrl", "description", "sections", "updatedAt", "createdAt");
        assertRequired(schemas, "RecipeSummaryDTO", "id", "name", "imageUrl", "description", "updatedAt", "createdAt");
        assertRequired(schemas, "RecipeSectionDTO", "id", "name", "position", "ingredients");
        assertRequired(schemas, "RecipeIngredientDTO", "id", "ingredientId", "amount", "unit", "note", "position");
        assertRequired(schemas, "UpdateRecipeRequest", "name", "imageUrl", "description", "sections");
        assertRequired(schemas, "UpdateRecipeSectionRequest", "name", "ingredients");
        assertRequired(schemas, "UpdateRecipeIngredientRequest", "ingredientId", "amount", "unit");
        assertOptional(schemas, "UpdateRecipeSectionRequest", "id");
        assertOptional(schemas, "UpdateRecipeIngredientRequest", "id");
        assertNullable(schemas, "RecipeIngredientDTO", "note");
        assertRequired(schemas, "IngredientDTO", "id", "name");
        assertThat(schemas.path("BadRequestProblemDetail").path("properties").path("errors").isMissingNode())
                .isFalse();
        assertThat(schemas.path("BadRequestProblemDetail")
                .path("properties")
                .path("errors")
                .path("additionalProperties")
                .path("type")
                .stringValue()).isEqualTo("string");
        assertStringLength(schemas, "CreateIngredientRequest", "name", 1, 255);
        assertOptional(schemas, "UpdateIngredientRequest", "name");
        assertNullable(schemas, "UpdateIngredientRequest", "name");
        assertStringLength(schemas, "UpdateIngredientRequest", "name", 1, 255);
        assertRequired(schemas, responseSchemaName(openApi, "/paths/~1recipes/get/responses/200/content/application~1json/schema"), "content", "page");
        assertRequired(schemas, responseSchemaName(openApi, "/paths/~1ingredients/get/responses/200/content/application~1json/schema"), "content", "page");
        assertRequired(schemas, "PageMetadata", "number", "size", "totalElements", "totalPages");
    }

    private void assertOperation(JsonNode openApi, String operationPointer, String operationId, String responseStatus) {
        assertThat(openApi.at(operationPointer + "/operationId").stringValue()).isEqualTo(operationId);
        assertThat(openApi.at(operationPointer + "/responses/" + responseStatus + "/content/application~1json").isMissingNode())
                .isFalse();
        assertThat(openApi.at(operationPointer + "/responses/" + responseStatus + "/content/*~1*").isMissingNode())
                .isTrue();
    }

    private String responseSchemaName(JsonNode openApi, String schemaPointer) {
        var schemaReference = openApi.at(schemaPointer + "/$ref").stringValue();
        return schemaReference.substring(schemaReference.lastIndexOf('/') + 1);
    }

    private void assertProblemDetailResponse(JsonNode openApi, String operationPointer, String responseStatus,
                                             String responseName) {
        var responseReference = openApi.at(operationPointer
                + "/responses/"
                + responseStatus
                + "/$ref");

        assertThat(responseReference.stringValue()).isEqualTo("#/components/responses/" + responseName);
    }

    private void assertProblemDetailComponent(JsonNode openApi, String responseName, String schemaName) {
        var schemaReference = openApi.at("/components/responses/"
                + responseName
                + "/content/application~1json/schema/$ref");

        assertThat(schemaReference.stringValue()).isEqualTo("#/components/schemas/" + schemaName);
    }

    private void assertQueryParameter(JsonNode openApi, String operationPointer, String parameterName) {
        var parameter = StreamSupport.stream(
                        openApi.at(operationPointer + "/parameters").spliterator(),
                        false
                )
                .filter(candidate -> candidate.path("name").stringValue().equals(parameterName))
                .findFirst();

        assertThat(parameter).isPresent().get()
                .satisfies(value -> {
                    assertThat(value.path("in").stringValue()).isEqualTo("query");
                    assertThat(value.path("required").booleanValue()).isTrue();
                    assertThat(value.path("schema").path("minLength").intValue()).isEqualTo(1);
                    assertThat(value.path("schema").path("maxLength").intValue()).isEqualTo(255);
                });
    }

    private void assertStringLength(JsonNode schemas, String schemaName, String propertyName,
                                    int minLength, int maxLength) {
        var property = schemas.path(schemaName).path("properties").path(propertyName);

        assertThat(property.path("minLength").intValue()).isEqualTo(minLength);
        assertThat(property.path("maxLength").intValue()).isEqualTo(maxLength);
    }

    private void assertRequired(JsonNode schemas, String schemaName, String... expectedRequiredFields) {
        var requiredFields = StreamSupport.stream(
                        schemas.path(schemaName).path("required").spliterator(),
                        false
                )
                .map(JsonNode::stringValue)
                .toList();

        assertThat(requiredFields).containsExactlyInAnyOrder(expectedRequiredFields);
    }

    private void assertNullable(JsonNode schemas, String schemaName, String propertyName) {
        var propertyTypes = StreamSupport.stream(
                        schemas.path(schemaName).path("properties").path(propertyName).path("type").spliterator(),
                        false
                )
                .map(JsonNode::stringValue)
                .toList();

        assertThat(propertyTypes).containsExactlyInAnyOrder("string", "null");
    }

    private void assertOptional(JsonNode schemas, String schemaName, String propertyName) {
        assertThat(schemas.path(schemaName).path("properties").has(propertyName)).isTrue();
        assertThat(StreamSupport.stream(schemas.path(schemaName).path("required").spliterator(), false)
                .map(JsonNode::stringValue))
                .doesNotContain(propertyName);
    }
}
