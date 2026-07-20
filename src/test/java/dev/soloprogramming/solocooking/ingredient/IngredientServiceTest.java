/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.DEFAULT_PAGEABLE;
import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientServiceTest {

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
                .name(IngredientTestConstants.NORMALIZED_INGREDIENT_INPUT)
                .build();

        // when
        var result = ingredientService.createIngredient(createIngredientRequest);

        // then
        assertThat(result.name()).isEqualTo(IngredientTestConstants.NORMALIZED_INGREDIENT_NAME);
        assertThat(ingredientRepository.findAll())
                .singleElement()
                .extracting(IngredientEntity::getName)
                .isEqualTo(IngredientTestConstants.NORMALIZED_INGREDIENT_NAME);
    }

    @Test
    void shouldCreateMultipleIngredientsWithUniqueIds() {
        // given
        var firstRequest = IngredientMother.createIngredientRequestBuilder().build();
        var secondRequest = IngredientMother.createIngredientRequestBuilder()
                .name(IngredientTestConstants.SECOND_INGREDIENT_NAME)
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
        var expectedMessage = IngredientTestConstants.DUPLICATED_INGREDIENT_MESSAGE;

        // when & then
        assertThatThrownBy(() -> ingredientService.createIngredient(createIngredientRequest))
                .isInstanceOfSatisfying(IngredientAlreadyExistsException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
    }

    @Test
    void shouldRejectDuplicatedIngredientAfterNormalization() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());
        var createIngredientRequest = IngredientMother.createIngredientRequestBuilder()
                .name(IngredientTestConstants.DUPLICATED_INGREDIENT_INPUT)
                .build();
        var expectedMessage = IngredientTestConstants.DUPLICATED_INGREDIENT_MESSAGE;

        // when & then
        assertThatThrownBy(() -> ingredientService.createIngredient(createIngredientRequest))
                .isInstanceOfSatisfying(IngredientAlreadyExistsException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
    }

    @Test
    void shouldSearchIngredientsByNormalizedNameFragment() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());
        ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.SECOND_INGREDIENT_NAME));
        ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.THIRD_INGREDIENT_NAME));
        var expectedIngredient = IngredientMother.ingredientDtoBuilder().build();

        // when
        var result = ingredientService.searchIngredients(
                IngredientTestConstants.INGREDIENT_SEARCH_INPUT,
                DEFAULT_PAGEABLE
        );

        // then
        assertThat(result.getContent())
                .usingRecursiveFieldByFieldElementComparator(defaultRecursiveComparisonConfiguration())
                .containsExactly(expectedIngredient);
    }

    @Test
    void shouldReturnIngredients() {
        // given
        ingredientRepository.save(IngredientMother.ingredientEntity());
        var second = ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.SECOND_INGREDIENT_NAME));
        var third = ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.THIRD_INGREDIENT_NAME));
        var expectedIngredients = List.of(
                IngredientMother.ingredientDtoBuilder().build(),
                IngredientDTO.builder().id(second.getId()).name(IngredientTestConstants.SECOND_INGREDIENT_NAME).build(),
                IngredientDTO.builder().id(third.getId()).name(IngredientTestConstants.THIRD_INGREDIENT_NAME).build()
        );
        var expectedPage = new PageImpl<>(expectedIngredients, DEFAULT_PAGEABLE, 3);

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
        // given
        var expectedMessage = IngredientTestConstants.MISSING_INGREDIENT_MESSAGE;

        // when & then
        assertThatThrownBy(() -> ingredientService.findById(IngredientTestConstants.MISSING_INGREDIENT_ID))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
    }

    @Test
    void shouldValidateExistingIngredients() {
        // given
        var first = ingredientRepository.save(IngredientMother.ingredientEntity());
        var second = ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.SECOND_INGREDIENT_NAME));
        var third = ingredientRepository.save(IngredientMother.ingredientEntityWithName(IngredientTestConstants.THIRD_INGREDIENT_NAME));

        // when & then
        assertThatCode(() -> ingredientService.validateIngredientsExist(
                        Set.of(first.getId(), second.getId(), third.getId())))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenValidatedIngredientDoesNotExist() {
        // given
        var expectedMessage = IngredientTestConstants.MISSING_INGREDIENTS_MESSAGE;

        // when & then
        assertThatThrownBy(() -> ingredientService.validateIngredientsExist(Set.of(IngredientTestConstants.MISSING_INGREDIENT_ID)))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
    }
}
