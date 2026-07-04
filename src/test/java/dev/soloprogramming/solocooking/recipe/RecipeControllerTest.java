/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static dev.soloprogramming.solocooking.common.TestResourceReader.readTestResource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecipeControllerTest {

    private static final String RECIPES_ENDPOINT = "/recipes";
    private static final String RECIPE_BY_ID_ENDPOINT = "/recipes/{recipeId}";
    private static final String GET_RECIPE_RESPONSE_RESOURCE = "controller/recipe/get-recipe-response.json";
    private static final String GET_RECIPES_RESPONSE_RESOURCE = "controller/recipe/get-recipes-response.json";
    private static final String EMPTY_RESPONSE_BODY = "";
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    private static final PageRequest MAX_PAGE_REQUEST = PageRequest.of(0, MAX_PAGE_SIZE);

    private final RecipeFacade recipeFacade = mock(RecipeFacade.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new RecipeController(recipeFacade))
            .setCustomArgumentResolvers(createPageableResolver())
            .build();

    @Test
    void shouldCreateRecipe() throws Exception {
        // given
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.createRecipe(createRecipeRequest)).willReturn(expectedRecipe);

        // when
        var result = mockMvc.perform(post(RECIPES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecipeRequest)));

        // then
        result.andExpectAll(
                status().isCreated(),
                content().json(readTestResource(GET_RECIPE_RESPONSE_RESOURCE), STRICT)
        );
        then(recipeFacade).should().createRecipe(createRecipeRequest);
    }

    @Test
    void shouldReturnRecipeById() throws Exception {
        // given
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.findById(RecipeTestConstants.RECIPE_ID)).willReturn(expectedRecipe);

        // when
        var result = mockMvc.perform(get(RECIPE_BY_ID_ENDPOINT, RecipeTestConstants.RECIPE_ID));

        // then
        result.andExpectAll(
                status().isOk(),
                content().json(readTestResource(GET_RECIPE_RESPONSE_RESOURCE), STRICT)
        );
        then(recipeFacade).should().findById(RecipeTestConstants.RECIPE_ID);
    }

    @Test
    void shouldReturnRecipes() throws Exception {
        // given
        var expectedRecipe = RecipeMother.recipeSummaryDtoBuilder().build();
        given(recipeFacade.getRecipes(DEFAULT_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(expectedRecipe), DEFAULT_PAGE_REQUEST, 1));

        // when
        var result = mockMvc.perform(get(RECIPES_ENDPOINT));

        // then
        result.andExpectAll(
                status().isOk(),
                content().json(readTestResource(GET_RECIPES_RESPONSE_RESOURCE), STRICT)
        );
        then(recipeFacade).should().getRecipes(DEFAULT_PAGE_REQUEST);
    }

    @Test
    void shouldClampPageSizeToMaximum() throws Exception {
        // given
        given(recipeFacade.getRecipes(MAX_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(), MAX_PAGE_REQUEST, 0));

        // when
        var result = mockMvc.perform(get(RECIPES_ENDPOINT)
                .param("size", "200"));

        // then
        result.andExpect(status().isOk());
        then(recipeFacade).should().getRecipes(MAX_PAGE_REQUEST);
    }

    @Test
    void shouldDeleteRecipeById() throws Exception {
        // when
        var result = mockMvc.perform(delete(RECIPE_BY_ID_ENDPOINT, RecipeTestConstants.RECIPE_ID));

        // then
        result.andExpectAll(
                status().isNoContent(),
                content().string(EMPTY_RESPONSE_BODY)
        );
        then(recipeFacade).should().deleteById(RecipeTestConstants.RECIPE_ID);
    }

    private static PageableHandlerMethodArgumentResolver createPageableResolver() {
        var resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(DEFAULT_PAGE_REQUEST);
        resolver.setMaxPageSize(MAX_PAGE_SIZE);
        return resolver;
    }
}
