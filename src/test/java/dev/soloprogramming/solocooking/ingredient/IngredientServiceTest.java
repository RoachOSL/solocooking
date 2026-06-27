/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.DEFAULT_PAGEABLE;
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
        // given
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        var expectedIngredientEntity = IngredientMother.ingredientEntity();

        // when
        var result = ingredientService.createIngredient(createIngredientRequest);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
        assertThat(ingredientRepository.findAll())
                .singleElement()
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredientEntity);
    }

    @Test
    void shouldNormalizeIngredientNameWhenCreatingIngredient() {
        // given
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder()
                .name("  Extra\tVirgin   Olive\nOil  ")
                .build();

        // when
        var result = ingredientService.createIngredient(createIngredientRequest);

        // then
        assertThat(result.name()).isEqualTo("extra virgin olive oil");
        assertThat(ingredientRepository.findAll())
                .singleElement()
                .extracting(IngredientEntity::getName)
                .isEqualTo("extra virgin olive oil");
    }

    @Test
    void shouldCreateMultipleIngredientsWithUniqueIds() {
        // given
        var firstRequest = IngredientMother.createIngredientRequestBuilder()
                .name("eggs")
                .build();
        var secondRequest = IngredientMother.createIngredientRequestBuilder()
                .name("milk")
                .build();

        // when
        var firstIngredient = ingredientService.createIngredient(firstRequest);
        var secondIngredient = ingredientService.createIngredient(secondRequest);

        // then
        assertThat(firstIngredient.id()).isNotEqualTo(secondIngredient.id());
        assertThat(ingredientRepository.findAll())
                .extracting(IngredientEntity::getId)
                .doesNotHaveDuplicates();
    }

    @Test
    void shouldRejectDuplicatedIngredient() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder().build();

        // when
        // then
        assertThatThrownBy(() -> ingredientService.createIngredient(createIngredientRequest))
                .isInstanceOf(IngredientAlreadyExistsException.class);
    }

    @Test
    void shouldRejectDuplicatedIngredientAfterNormalization() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder()
                .name("  EGGS  ")
                .build();

        // when
        // then
        assertThatThrownBy(() -> ingredientService.createIngredient(createIngredientRequest))
                .isInstanceOf(IngredientAlreadyExistsException.class);
    }

    @Test
    void shouldReturnIngredients() {
        // given
        var ingredientEntity = IngredientMother.ingredientEntity();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        var expectedPage = new PageImpl<>(List.of(expectedIngredient), DEFAULT_PAGEABLE, 1);
        ingredientRepository.save(ingredientEntity);

        // when
        var result = ingredientService.getIngredients(DEFAULT_PAGEABLE);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedPage);
    }

    @Test
    void shouldReturnIngredientById() {
        // given
        var ingredientEntity = IngredientMother.ingredientEntity();
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();
        ingredientRepository.save(ingredientEntity);

        // when
        var result = ingredientService.findById(IngredientTestConstants.INGREDIENT_ID);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedIngredient);
    }

    @Test
    void shouldThrowWhenIngredientDoesNotExist() {
        // when
        // then
        assertThatThrownBy(() -> ingredientService.findById(MISSING_INGREDIENT_ID))
                .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void shouldValidateExistingIngredients() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());

        // when
        // then
        assertThatCode(() -> ingredientService.validateIngredientsExist(Set.of(IngredientTestConstants.INGREDIENT_ID)))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenValidatedIngredientDoesNotExist() {
        // when
        // then
        assertThatThrownBy(() -> ingredientService.validateIngredientsExist(Set.of(MISSING_INGREDIENT_ID)))
                .isInstanceOf(IngredientNotFoundException.class);
    }
}
