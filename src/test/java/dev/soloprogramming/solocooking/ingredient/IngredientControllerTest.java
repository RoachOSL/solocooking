/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IngredientControllerTest {

    private static final String INGREDIENTS_ENDPOINT = "/ingredients";
    private static final String INGREDIENT_BY_ID_ENDPOINT = "/ingredients/{ingredientId}";
    private static final String GET_INGREDIENT_RESPONSE_RESOURCE = "controller/ingredient/get-ingredient-response.json";
    private static final String GET_INGREDIENTS_RESPONSE_RESOURCE = "controller/ingredient/get-ingredients-response.json";
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    private static final PageRequest MAX_PAGE_REQUEST = PageRequest.of(0, MAX_PAGE_SIZE);

    private final IngredientFacade ingredientFacade = mock(IngredientFacade.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new IngredientController(ingredientFacade))
            .setCustomArgumentResolvers(createPageableResolver())
            .build();

    @Test
    void shouldCreateIngredient() throws Exception {
        // given
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.createIngredient(createIngredientRequest)).willReturn(expectedIngredient);

        // when
        var result = mockMvc.perform(post(INGREDIENTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createIngredientRequest)));

        // then
        result.andExpectAll(
                status().isCreated(),
                content().json(readTestResource(GET_INGREDIENT_RESPONSE_RESOURCE), STRICT)
        );
        then(ingredientFacade).should().createIngredient(createIngredientRequest);
    }

    @Test
    void shouldReturnIngredientById() throws Exception {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.findById(IngredientTestConstants.INGREDIENT_ID)).willReturn(expectedIngredient);

        // when
        var result = mockMvc.perform(get(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID));

        // then
        result.andExpectAll(
                status().isOk(),
                content().json(readTestResource(GET_INGREDIENT_RESPONSE_RESOURCE), STRICT)
        );
        then(ingredientFacade).should().findById(IngredientTestConstants.INGREDIENT_ID);
    }

    @Test
    void shouldReturnIngredients() throws Exception {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.getIngredients(DEFAULT_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(expectedIngredient), DEFAULT_PAGE_REQUEST, 1));

        // when
        var result = mockMvc.perform(get(INGREDIENTS_ENDPOINT));

        // then
        result.andExpectAll(
                status().isOk(),
                content().json(readTestResource(GET_INGREDIENTS_RESPONSE_RESOURCE), STRICT)
        );
        then(ingredientFacade).should().getIngredients(DEFAULT_PAGE_REQUEST);
    }

    @Test
    void shouldClampPageSizeToMaximum() throws Exception {
        // given
        given(ingredientFacade.getIngredients(MAX_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(), MAX_PAGE_REQUEST, 0));

        // when
        var result = mockMvc.perform(get(INGREDIENTS_ENDPOINT)
                .param("size", "200"));

        // then
        result.andExpect(status().isOk());
        then(ingredientFacade).should().getIngredients(MAX_PAGE_REQUEST);
    }

    private static PageableHandlerMethodArgumentResolver createPageableResolver() {
        var resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(DEFAULT_PAGE_REQUEST);
        resolver.setMaxPageSize(MAX_PAGE_SIZE);
        return resolver;
    }
}
