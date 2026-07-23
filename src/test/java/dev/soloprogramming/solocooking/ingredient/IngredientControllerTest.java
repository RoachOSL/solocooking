/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;

import dev.soloprogramming.solocooking.common.exception.InvalidSortPropertyException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientInUseException;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    private static final String INGREDIENTS_ENDPOINT = API_SERVLET_PATH + "/ingredients";
    private static final String INGREDIENT_SEARCH_ENDPOINT = INGREDIENTS_ENDPOINT + "/search";
    private static final String INGREDIENT_BY_ID_ENDPOINT = API_SERVLET_PATH + "/ingredients/{ingredientId}";
    private static final String GET_INGREDIENT_RESPONSE_RESOURCE = "controller/ingredient/get-ingredient-response.json";
    private static final String GET_INGREDIENTS_RESPONSE_RESOURCE = "controller/ingredient/get-ingredients-response.json";
    private static final String CREATE_INGREDIENT_CONFLICT_RESPONSE_RESOURCE =
            "controller/ingredient/create-ingredient-conflict-response.json";
    private static final String DELETE_INGREDIENT_CONFLICT_RESPONSE_RESOURCE =
            "controller/ingredient/delete-ingredient-conflict-response.json";
    private static final String UPDATE_INGREDIENT_VALIDATION_ERROR_RESPONSE_RESOURCE =
            "controller/ingredient/update-ingredient-validation-error-response.json";
    private static final String SEARCH_INGREDIENT_MISSING_NAME_VALIDATION_ERROR_RESPONSE_RESOURCE =
            "controller/ingredient/search-ingredient-missing-name-validation-error-response.json";
    private static final String SEARCH_INGREDIENT_BLANK_NAME_VALIDATION_ERROR_RESPONSE_RESOURCE =
            "controller/ingredient/search-ingredient-blank-name-validation-error-response.json";
    private static final String GET_INGREDIENTS_INVALID_SORT_RESPONSE_RESOURCE =
            "controller/ingredient/get-ingredients-invalid-sort-response.json";

    @MockitoBean
    private IngredientFacade ingredientFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldCreateIngredient() {
        // given
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.createIngredient(createIngredientRequest)).willReturn(expectedIngredient);

        // when & then
        assertThat(post()
                .uri(INGREDIENTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createIngredientRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENT_RESPONSE_RESOURCE));
    }

    @Test
    void shouldReturnTypedConflictWhenIngredientAlreadyExists() {
        // given
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();
        given(ingredientFacade.createIngredient(createIngredientRequest))
                .willThrow(IngredientAlreadyExistsException.byName(IngredientTestConstants.INGREDIENT_STORED_NAME));

        // when & then
        assertThat(post()
                .uri(INGREDIENTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createIngredientRequest)))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(CREATE_INGREDIENT_CONFLICT_RESPONSE_RESOURCE));
    }

    @Test
    void shouldUpdateIngredient() {
        // given
        var updateRequest = IngredientMother.updateIngredientRequestBuilder().build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.updateIngredient(IngredientTestConstants.INGREDIENT_ID, updateRequest))
                .willReturn(expectedIngredient);

        // when & then
        assertThat(patch()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENT_RESPONSE_RESOURCE));
        then(ingredientFacade).should().updateIngredient(IngredientTestConstants.INGREDIENT_ID, updateRequest);
    }

    @Test
    void shouldDelegateNullIngredientNameUpdate() {
        // given
        var updateRequest = UpdateIngredientRequest.builder().name(null).build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.updateIngredient(IngredientTestConstants.INGREDIENT_ID, updateRequest))
                .willReturn(expectedIngredient);

        // when & then
        assertThat(patch()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .hasStatusOk();
        then(ingredientFacade).should().updateIngredient(IngredientTestConstants.INGREDIENT_ID, updateRequest);
    }

    @Test
    void shouldRejectBlankIngredientNameUpdate() {
        // given
        var updateRequest = UpdateIngredientRequest.builder().name(" ").build();

        // when
        assertThat(patch()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(UPDATE_INGREDIENT_VALIDATION_ERROR_RESPONSE_RESOURCE));

        // then
        then(ingredientFacade).shouldHaveNoInteractions();
    }

    @Test
    void shouldRejectOversizedIngredientNameUpdate() {
        // given
        var updateRequest = UpdateIngredientRequest.builder().name("i".repeat(256)).build();

        // when
        assertThat(patch()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .hasStatus(HttpStatus.BAD_REQUEST);

        // then
        then(ingredientFacade).shouldHaveNoInteractions();
    }

    @Test
    void shouldDeleteIngredient() {
        // when
        assertThat(delete()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID))
                .hasStatus(HttpStatus.NO_CONTENT);

        // then
        then(ingredientFacade).should().deleteById(IngredientTestConstants.INGREDIENT_ID);
    }

    @Test
    void shouldReturnTypedConflictWhenIngredientIsInUse() {
        // given
        willThrow(IngredientInUseException.forDeletion())
                .given(ingredientFacade)
                .deleteById(IngredientTestConstants.INGREDIENT_ID);

        // when & then
        assertThat(delete()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(DELETE_INGREDIENT_CONFLICT_RESPONSE_RESOURCE));
    }

    @Test
    void shouldReturnIngredientById() {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.findById(IngredientTestConstants.INGREDIENT_ID)).willReturn(expectedIngredient);

        // when & then
        assertThat(get()
                .uri(INGREDIENT_BY_ID_ENDPOINT, IngredientTestConstants.INGREDIENT_ID))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENT_RESPONSE_RESOURCE));
    }

    @Test
    void shouldRejectMissingIngredientNameSearch() {
        // when
        assertThat(get()
                .uri(INGREDIENT_SEARCH_ENDPOINT))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(
                        SEARCH_INGREDIENT_MISSING_NAME_VALIDATION_ERROR_RESPONSE_RESOURCE
                ));

        // then
        then(ingredientFacade).shouldHaveNoInteractions();
    }

    @Test
    void shouldRejectBlankIngredientNameSearch() {
        // when
        assertThat(get()
                .uri(INGREDIENT_SEARCH_ENDPOINT)
                .param("name", " "))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(
                        SEARCH_INGREDIENT_BLANK_NAME_VALIDATION_ERROR_RESPONSE_RESOURCE
                ));

        // then
        then(ingredientFacade).shouldHaveNoInteractions();
    }

    @Test
    void shouldSearchIngredientsByName() {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.searchIngredients(
                IngredientTestConstants.INGREDIENT_SEARCH_INPUT,
                DEFAULT_WEB_PAGE_REQUEST
        )).willReturn(new PageImpl<>(List.of(expectedIngredient), DEFAULT_WEB_PAGE_REQUEST, 1));

        // when & then
        assertThat(get()
                .uri(INGREDIENT_SEARCH_ENDPOINT)
                .param("name", IngredientTestConstants.INGREDIENT_SEARCH_INPUT))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENTS_RESPONSE_RESOURCE));
        then(ingredientFacade).should().searchIngredients(
                IngredientTestConstants.INGREDIENT_SEARCH_INPUT,
                DEFAULT_WEB_PAGE_REQUEST
        );
    }

    @Test
    void shouldReturnIngredients() {
        // given
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        given(ingredientFacade.getIngredients(DEFAULT_WEB_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(expectedIngredient), DEFAULT_WEB_PAGE_REQUEST, 1));

        // when & then
        assertThat(get()
                .uri(INGREDIENTS_ENDPOINT))
                .hasStatusOk()
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENTS_RESPONSE_RESOURCE));
    }

    @Test
    void shouldRejectUnsupportedIngredientSortProperty() {
        // given
        var pageable = PageRequest.of(0, 20, Sort.by("description"));
        given(ingredientFacade.getIngredients(pageable))
                .willThrow(InvalidSortPropertyException.forProperty("description", Set.of("id", "name")));

        // when & then
        assertThat(get()
                .uri(INGREDIENTS_ENDPOINT)
                .param("sort", "description"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isStrictlyEqualTo(readTestResource(GET_INGREDIENTS_INVALID_SORT_RESPONSE_RESOURCE));
    }

    @Test
    void shouldClampPageSizeToMaximum() {
        // given
        given(ingredientFacade.getIngredients(MAX_WEB_PAGE_REQUEST))
                .willReturn(new PageImpl<>(List.of(), MAX_WEB_PAGE_REQUEST, 0));

        // when & then
        assertThat(get()
                .uri(INGREDIENTS_ENDPOINT)
                .param("size", OVERSIZED_WEB_PAGE_SIZE))
                .hasStatusOk();
        then(ingredientFacade).should().getIngredients(MAX_WEB_PAGE_REQUEST);
    }

    private MockMvcTester.MockMvcRequestBuilder get() {
        return mockMvcTester.get().servletPath(API_SERVLET_PATH);
    }

    private MockMvcTester.MockMvcRequestBuilder post() {
        return mockMvcTester.post().servletPath(API_SERVLET_PATH);
    }

    private MockMvcTester.MockMvcRequestBuilder patch() {
        return mockMvcTester.patch().servletPath(API_SERVLET_PATH);
    }

    private MockMvcTester.MockMvcRequestBuilder delete() {
        return mockMvcTester.delete().servletPath(API_SERVLET_PATH);
    }
}
