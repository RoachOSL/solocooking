/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

    private final IngredientFacade ingredientFacade = mock(IngredientFacade.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new IngredientController(ingredientFacade))
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
}
