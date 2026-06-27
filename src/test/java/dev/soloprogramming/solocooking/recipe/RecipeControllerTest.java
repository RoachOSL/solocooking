/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static dev.soloprogramming.solocooking.common.TestResourceReader.readTestResource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecipeControllerTest {

    private static final String RECIPE_RESPONSE_RESOURCE = "controller/recipe/recipe-response.json";

    private final RecipeFacade recipeFacade = mock(RecipeFacade.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new RecipeController(recipeFacade))
            .build();

    @Test
    void shouldCreateRecipe() throws Exception {
        // given
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.createRecipe(createRecipeRequest)).willReturn(expectedRecipe);

        // when
        var result = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecipeRequest)));

        // then
        result.andExpectAll(
                status().isCreated(),
                content().json(readTestResource(RECIPE_RESPONSE_RESOURCE), true)
        );
        then(recipeFacade).should().createRecipe(createRecipeRequest);
    }

    @Test
    void shouldReturnRecipeById() throws Exception {
        // given
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.findById(RecipeTestConstants.RECIPE_ID)).willReturn(expectedRecipe);

        // when
        var result = mockMvc.perform(get("/recipes/{recipeId}", RecipeTestConstants.RECIPE_ID));

        // then
        result.andExpectAll(
                status().isOk(),
                content().json(readTestResource(RECIPE_RESPONSE_RESOURCE), true)
        );
        then(recipeFacade).should().findById(RecipeTestConstants.RECIPE_ID);
    }

    @Test
    void shouldDeleteRecipeById() throws Exception {
        // when
        var result = mockMvc.perform(delete("/recipes/{recipeId}", RecipeTestConstants.RECIPE_ID));

        // then
        result.andExpectAll(
                status().isNoContent(),
                content().string("")
        );
        then(recipeFacade).should().deleteById(RecipeTestConstants.RECIPE_ID);
    }
}
