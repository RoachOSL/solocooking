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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IngredientControllerTest {

    private static final String INGREDIENT_RESPONSE_RESOURCE = "controller/ingredient/ingredient-response.json";

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
        var result = mockMvc.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createIngredientRequest)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().json(readTestResource(INGREDIENT_RESPONSE_RESOURCE), true));
        then(ingredientFacade).should().createIngredient(createIngredientRequest);
    }

    @Test
    void shouldReturnIngredientById() throws Exception {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.findById(IngredientTestConstants.INGREDIENT_ID)).willReturn(expectedIngredient);

        // when
        var result = mockMvc.perform(get("/ingredients/{ingredientId}", IngredientTestConstants.INGREDIENT_ID));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(readTestResource(INGREDIENT_RESPONSE_RESOURCE), true));
        then(ingredientFacade).should().findById(IngredientTestConstants.INGREDIENT_ID);
    }
}
