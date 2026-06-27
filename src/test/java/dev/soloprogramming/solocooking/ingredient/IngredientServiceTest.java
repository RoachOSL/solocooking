/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientServiceTest {

    private static final UUID MISSING_INGREDIENT_ID = UUID.fromString("af4733da-7ded-4a07-9f92-c8fd5d479b76");

    private final InMemoryIngredientRepository ingredientRepository = new InMemoryIngredientRepository();
    private final IngredientMapper ingredientMapper = new IngredientMapperImpl();
    private final IngredientService ingredientService = new IngredientService(ingredientRepository, ingredientMapper);

    @Test
    void shouldCreateIngredient() {
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        var expectedIngredientEntity = IngredientMother.ingredientEntity();

        var result = ingredientService.createIngredient(createIngredientRequest);

        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
        assertThat(ingredientRepository.findAll())
                .singleElement()
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredientEntity);
    }

    @Test
    void shouldRejectDuplicatedIngredient() {
        ingredientRepository.save(IngredientMother.ingredientEntity());
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();

        assertThatThrownBy(() -> ingredientService.createIngredient(createIngredientRequest))
                .isInstanceOf(IngredientAlreadyExistsException.class);
    }

    @Test
    void shouldReturnIngredients() {
        var pageable = PageRequest.of(0, 10);
        var ingredientEntity = IngredientMother.ingredientEntity();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        var expectedPage = new PageImpl<>(List.of(expectedIngredient), pageable, 1);
        ingredientRepository.save(ingredientEntity);

        var result = ingredientService.getIngredients(pageable);

        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedPage);
    }

    @Test
    void shouldReturnIngredientById() {
        var ingredientEntity = IngredientMother.ingredientEntity();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        ingredientRepository.save(ingredientEntity);

        var result = ingredientService.findById(IngredientTestConstants.INGREDIENT_ID);

        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
    }

    @Test
    void shouldThrowWhenIngredientDoesNotExist() {
        assertThatThrownBy(() -> ingredientService.findById(MISSING_INGREDIENT_ID))
                .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void shouldValidateExistingIngredients() {
        ingredientRepository.save(IngredientMother.ingredientEntity());

        assertThatCode(() -> ingredientService.validateExist(List.of(IngredientTestConstants.INGREDIENT_ID)))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenValidatedIngredientDoesNotExist() {
        assertThatThrownBy(() -> ingredientService.validateExist(List.of(MISSING_INGREDIENT_ID)))
                .isInstanceOf(IngredientNotFoundException.class);
    }
}
