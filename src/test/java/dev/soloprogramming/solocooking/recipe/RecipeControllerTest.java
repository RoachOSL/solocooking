/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.API_SERVLET_PATH;
import static dev.soloprogramming.solocooking.common.CommonTestConstants.DEFAULT_WEB_PAGE_REQUEST;
import static dev.soloprogramming.solocooking.common.CommonTestConstants.MAX_WEB_PAGE_REQUEST;
import static dev.soloprogramming.solocooking.common.CommonTestConstants.OVERSIZED_WEB_PAGE_SIZE;
import static dev.soloprogramming.solocooking.common.TestResourceReader.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    private static final String RECIPES_ENDPOINT = API_SERVLET_PATH + "/recipes";
    private static final String RECIPE_BY_ID_ENDPOINT = API_SERVLET_PATH + "/recipes/{recipeId}";
    private static final String GET_RECIPE_RESPONSE_RESOURCE = "controller/recipe/get-recipe-response.json";
    private static final String GET_RECIPES_RESPONSE_RESOURCE = "controller/recipe/get-recipes-response.json";
    private static final String EMPTY_RESPONSE_BODY = "";

    @MockitoBean
    private RecipeFacade recipeFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldCreateRecipe() {
        // given
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.createRecipe(createRecipeRequest)).willReturn(expectedRecipe);

        // when & then
        assertThat(mockMvcTester.post()
                .uri(RECIPES_ENDPOINT)
                .servletPath(API_SERVLET_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecipeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_RECIPE_RESPONSE_RESOURCE));
    }

    @Test
    void shouldReturnRecipeById() {
        // given
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        given(recipeFacade.findById(RecipeTestConstants.RECIPE_ID)).willReturn(expectedRecipe);

        // when & then
        assertThat(mockMvcTester.get()
                .uri(RECIPE_BY_ID_ENDPOINT, RecipeTestConstants.RECIPE_ID)
                .servletPath(API_SERVLET_PATH))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_RECIPE_RESPONSE_RESOURCE));
    }

    @Test
    void shouldReturnRecipes() {
        // given
        var expectedRecipe = RecipeMother.recipeSummaryDtoBuilder().build();
        given(recipeFacade.getRecipes(DEFAULT_WEB_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(expectedRecipe), DEFAULT_WEB_PAGE_REQUEST, 1));

        // when & then
        assertThat(mockMvcTester.get()
                .uri(RECIPES_ENDPOINT)
                .servletPath(API_SERVLET_PATH))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_RECIPES_RESPONSE_RESOURCE));
    }

    @Test
    void shouldClampPageSizeToMaximum() {
        // given
        given(recipeFacade.getRecipes(MAX_WEB_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(), MAX_WEB_PAGE_REQUEST, 0));

        // when & then
        assertThat(mockMvcTester.get()
                .uri(RECIPES_ENDPOINT)
                .servletPath(API_SERVLET_PATH)
                .param("size", OVERSIZED_WEB_PAGE_SIZE))
                .hasStatusOk();
        then(recipeFacade).should().getRecipes(MAX_WEB_PAGE_REQUEST);
    }

    @Test
    void shouldDeleteRecipeById() {
        // when & then
        assertThat(mockMvcTester.delete()
                .uri(RECIPE_BY_ID_ENDPOINT, RecipeTestConstants.RECIPE_ID)
                .servletPath(API_SERVLET_PATH))
                .hasStatus(HttpStatus.NO_CONTENT)
                .hasBodyTextEqualTo(EMPTY_RESPONSE_BODY);
        then(recipeFacade).should().deleteById(RecipeTestConstants.RECIPE_ID);
    }
}
