/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static dev.soloprogramming.solocooking.ingredient.IngredientTestFixtures.givenExistingIngredient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientFacadeIT extends BaseIntegrationTest {

    @Autowired
    private IngredientFacade ingredientFacade;

    @Test
    void shouldCreateIngredient() {
        // given
        var request = CreateIngredientRequest.builder()
                .name(IngredientTestConstants.NORMALIZED_INGREDIENT_INPUT)
                .build();
        var expectedIngredient = IngredientDTO.builder()
                .name(IngredientTestConstants.NORMALIZED_INGREDIENT_NAME)
                .build();

        // when
        var result = ingredientFacade.createIngredient(request);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
    }

    @Test
    void shouldRejectDuplicatedIngredientNameAfterNormalization() {
        // given
        givenExistingIngredient(ingredientFacade);
        var duplicatedRequest = CreateIngredientRequest.builder()
                .name(IngredientTestConstants.DUPLICATED_INGREDIENT_INPUT)
                .build();

        // when & then
        assertThatThrownBy(() -> ingredientFacade.createIngredient(duplicatedRequest))
                .isInstanceOfSatisfying(IngredientAlreadyExistsException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(IngredientTestConstants.DUPLICATED_INGREDIENT_MESSAGE)
                );
    }

    @Test
    void shouldReturnIngredientsPage() {
        // given
        var expectedIngredient = givenExistingIngredient(ingredientFacade);

        // when
        var result = ingredientFacade.getIngredients(Pageable.unpaged());

        // then
        assertThat(result.getContent())
                .usingRecursiveFieldByFieldElementComparator(defaultRecursiveComparisonConfiguration())
                .containsExactly(expectedIngredient);
    }

    @Test
    void shouldFindIngredientById() {
        // given
        var expectedIngredient = givenExistingIngredient(ingredientFacade);

        // when
        var result = ingredientFacade.findById(expectedIngredient.id());

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
    }

    @Test
    void shouldThrowWhenIngredientDoesNotExist() {
        // when & then
        assertThatThrownBy(() -> ingredientFacade.findById(IngredientTestConstants.MISSING_INGREDIENT_ID))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(IngredientTestConstants.MISSING_INGREDIENT_MESSAGE)
                );
    }
}
