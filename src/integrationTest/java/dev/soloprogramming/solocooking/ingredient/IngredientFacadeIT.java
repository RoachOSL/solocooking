/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientInUseException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import dev.soloprogramming.solocooking.recipe.RecipeFacade;
import dev.soloprogramming.solocooking.recipe.RecipeTestFixtures;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static dev.soloprogramming.solocooking.ingredient.IngredientTestFixtures.givenExistingIngredient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientFacadeIT extends BaseIntegrationTest {

    private static final String MAX_INGREDIENT_NAME = "a".repeat(255);
    private static final String MAX_EXPANDING_INGREDIENT_NAME = "\u0130".repeat(255);

    @Autowired
    private IngredientFacade ingredientFacade;

    @Autowired
    private RecipeFacade recipeFacade;

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
    void shouldAcceptMaximumNormalizedNameWhenCreatingAndUpdating() {
        // given
        var ingredient = ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name(MAX_INGREDIENT_NAME)
                .build());
        var maximumUpdatedName = "b".repeat(255);

        // when
        var updatedIngredient = ingredientFacade.updateIngredient(
                ingredient.id(),
                UpdateIngredientRequest.builder().name(maximumUpdatedName).build()
        );

        // then
        assertThat(ingredient.name()).isEqualTo(MAX_INGREDIENT_NAME);
        assertThat(updatedIngredient.name()).isEqualTo(maximumUpdatedName);
    }

    @Test
    void shouldRejectCreateWhenNormalizedNameExceedsMaximumLength() {
        // given
        var request = CreateIngredientRequest.builder()
                .name(MAX_EXPANDING_INGREDIENT_NAME)
                .build();

        // when & then
        assertThatThrownBy(() -> ingredientFacade.createIngredient(request))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldRejectUpdateWhenNormalizedNameExceedsMaximumLength() {
        // given
        var ingredient = givenExistingIngredient(ingredientFacade);
        var request = UpdateIngredientRequest.builder()
                .name(MAX_EXPANDING_INGREDIENT_NAME)
                .build();

        // when & then
        assertThatThrownBy(() -> ingredientFacade.updateIngredient(ingredient.id(), request))
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(ingredientFacade.findById(ingredient.id())).isEqualTo(ingredient);
    }

    @Test
    void shouldNormalizeUnicodeWhitespaceWhenCreatingAndUpdating() {
        // given
        var createRequest = CreateIngredientRequest.builder()
                .name("\u00a0Extra\u2003Virgin\u00a0")
                .build();

        // when
        var ingredient = ingredientFacade.createIngredient(createRequest);
        var updatedIngredient = ingredientFacade.updateIngredient(
                ingredient.id(),
                UpdateIngredientRequest.builder().name("\u00a0Rice\u2003Flour\u00a0").build()
        );

        // then
        assertThat(ingredient.name()).isEqualTo("extra virgin");
        assertThat(updatedIngredient.name()).isEqualTo("rice flour");
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
    void shouldRejectDuplicatedIngredientNameWithUnicodeWhitespaceWhenCreating() {
        // given
        ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name("olive oil")
                .build());
        var duplicatedRequest = CreateIngredientRequest.builder()
                .name("olive\u2003oil")
                .build();

        // when & then
        assertThatThrownBy(() -> ingredientFacade.createIngredient(duplicatedRequest))
                .isInstanceOf(IngredientAlreadyExistsException.class);
    }

    @Test
    void shouldRejectDuplicatedIngredientNameWithUnicodeWhitespaceWhenUpdating() {
        // given
        ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name("olive oil")
                .build());
        var ingredient = ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name("milk")
                .build());
        var duplicatedRequest = UpdateIngredientRequest.builder()
                .name("olive\u2003oil")
                .build();

        // when & then
        assertThatThrownBy(() -> ingredientFacade.updateIngredient(ingredient.id(), duplicatedRequest))
                .isInstanceOf(IngredientAlreadyExistsException.class);
    }

    @Test
    void shouldPersistIngredientUpdate() {
        // given
        var ingredient = givenExistingIngredient(ingredientFacade);
        var request = UpdateIngredientRequest.builder()
                .name(IngredientTestConstants.NORMALIZED_INGREDIENT_INPUT)
                .build();

        // when
        var updatedIngredient = ingredientFacade.updateIngredient(ingredient.id(), request);
        var persistedIngredient = ingredientFacade.findById(ingredient.id());

        // then
        assertThat(updatedIngredient.name()).isEqualTo(IngredientTestConstants.NORMALIZED_INGREDIENT_NAME);
        assertThat(persistedIngredient).isEqualTo(updatedIngredient);
    }

    @Test
    void shouldDeleteIngredientIdempotently() {
        // given
        var ingredient = givenExistingIngredient(ingredientFacade);

        // when & then
        assertThatCode(() -> {
            ingredientFacade.deleteById(ingredient.id());
            ingredientFacade.deleteById(ingredient.id());
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> ingredientFacade.findById(ingredient.id()))
                .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void shouldRejectDeletingIngredientUsedByRecipe() {
        // given
        var ingredient = givenExistingIngredient(ingredientFacade);
        RecipeTestFixtures.givenExistingRecipe(recipeFacade, ingredient.id());

        // when & then
        assertThatThrownBy(() -> ingredientFacade.deleteById(ingredient.id()))
                .isInstanceOf(IngredientInUseException.class);
        assertThat(ingredientFacade.findById(ingredient.id())).isEqualTo(ingredient);
    }

    @Test
    void shouldSearchIngredientsByNormalizedNameFragment() {
        // given
        var expectedIngredient = givenExistingIngredient(ingredientFacade);
        ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name(IngredientTestConstants.SECOND_INGREDIENT_NAME)
                .build());

        // when
        var result = ingredientFacade.searchIngredients(
                IngredientTestConstants.INGREDIENT_SEARCH_INPUT,
                Pageable.unpaged()
        );

        // then
        assertThat(result.getContent())
                .usingRecursiveFieldByFieldElementComparator(defaultRecursiveComparisonConfiguration())
                .containsExactly(expectedIngredient);
    }

    @Test
    void shouldReturnIngredientsPage() {
        // given
        ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name(IngredientTestConstants.SECOND_INGREDIENT_NAME)
                .build());
        var expectedIngredient = ingredientFacade.createIngredient(CreateIngredientRequest.builder()
                .name(IngredientTestConstants.THIRD_INGREDIENT_NAME)
                .build());
        var pageable = PageRequest.of(0, 1, Sort.by("name"));

        // when
        var result = ingredientFacade.getIngredients(pageable);

        // then
        assertThat(result.getContent())
                .usingRecursiveFieldByFieldElementComparator(defaultRecursiveComparisonConfiguration())
                .containsExactly(expectedIngredient);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isOne();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
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
