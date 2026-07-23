/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import dev.soloprogramming.solocooking.common.exception.InvalidSortPropertyException;
import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.recipe.exception.InvalidRecipeChildIdException;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.DEFAULT_PAGEABLE;
import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

class RecipeServiceTest {

    private static final String UPDATED_NAME = "Updated carbonara";
    private static final String UPDATED_DESCRIPTION = "Updated description";
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.valueOf(4);
    private static final String UPDATED_UNIT = "whole";
    private static final String UPDATED_NOTE = "room temperature";

    private final InMemoryRecipeRepository recipeRepository = new InMemoryRecipeRepository();
    private final RecipeMapper recipeMapper = new RecipeMapperImpl();
    private final RecipeFactory recipeFactory = new RecipeFactory();
    private final IngredientFacade ingredientFacade = mock(IngredientFacade.class);
    private final RecipeService recipeService =
            new RecipeService(recipeRepository, recipeMapper, recipeFactory, ingredientFacade);

    @Test
    void shouldCreateRecipe() {
        // given
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        var expectedRecipeEntity = RecipeMother.recipeEntity();

        // when
        var result = recipeService.createRecipe(createRecipeRequest);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
        assertThat(recipeRepository.findAll())
                .singleElement()
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipeEntity);
        then(ingredientFacade).should().validateIngredientsExist(Set.of(RecipeTestConstants.INGREDIENT_ID));
    }

    @Test
    void shouldRejectRecipeWhenIngredientDoesNotExist() {
        // given
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder()
                .sections(List.of(RecipeMother.createRecipeSectionRequestBuilder()
                        .ingredients(List.of(RecipeMother.createRecipeIngredientRequestBuilder()
                                .ingredientId(RecipeTestConstants.MISSING_INGREDIENT_ID)
                                .build()))
                        .build()))
                .build();
        willThrow(IngredientNotFoundException.byIngredientIds(Set.of(RecipeTestConstants.MISSING_INGREDIENT_ID)))
                .given(ingredientFacade)
                .validateIngredientsExist(Set.of(RecipeTestConstants.MISSING_INGREDIENT_ID));
        var expectedMessage = RecipeTestConstants.MISSING_INGREDIENTS_MESSAGE;

        // when & then
        assertThatThrownBy(() -> recipeService.createRecipe(createRecipeRequest))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
        assertThat(recipeRepository.findAll()).isEmpty();
    }

    @Test
    void shouldUpdateRecipeAndPreserveChildIds() {
        // given
        recipeRepository.save(RecipeMother.recipeEntity());
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .sections(List.of(RecipeMother.updateRecipeSectionRequestBuilder()
                        .ingredients(List.of(RecipeMother.updateRecipeIngredientRequestBuilder()
                                .amount(UPDATED_AMOUNT)
                                .unit(UPDATED_UNIT)
                                .note(UPDATED_NOTE)
                                .build()))
                        .build()))
                .build();

        // when
        var result = recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request);

        // then
        assertThat(result.name()).isEqualTo(UPDATED_NAME);
        assertThat(result.description()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(result.sections()).singleElement().satisfies(section -> {
            assertThat(section.id()).isEqualTo(RecipeTestConstants.RECIPE_SECTION_ID);
            assertThat(section.ingredients()).singleElement().satisfies(ingredient -> {
                assertThat(ingredient.id()).isEqualTo(RecipeTestConstants.RECIPE_INGREDIENT_ID);
                assertThat(ingredient.amount()).isEqualByComparingTo(UPDATED_AMOUNT);
                assertThat(ingredient.unit()).isEqualTo(UPDATED_UNIT);
                assertThat(ingredient.note()).isEqualTo(UPDATED_NOTE);
            });
        });
        then(ingredientFacade).should().validateIngredientsExist(Set.of(RecipeTestConstants.INGREDIENT_ID));
    }

    @Test
    void shouldRejectDuplicateSectionIdWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntity());
        var duplicateSection = RecipeMother.updateRecipeSectionRequestBuilder().build();
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .sections(List.of(duplicateSection, duplicateSection))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOfSatisfying(InvalidRecipeChildIdException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                );
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
    }

    @Test
    void shouldRejectDuplicateIngredientChildIdWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntity());
        var duplicateIngredient = RecipeMother.updateRecipeIngredientRequestBuilder().build();
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .sections(List.of(RecipeMother.updateRecipeSectionRequestBuilder()
                        .ingredients(List.of(duplicateIngredient, duplicateIngredient))
                        .build()))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOf(InvalidRecipeChildIdException.class);
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
    }

    @Test
    void shouldRejectForeignSectionIdWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntity());
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .sections(List.of(RecipeMother.updateRecipeSectionRequestBuilder()
                        .id(RecipeTestConstants.SECOND_RECIPE_SECTION_ID)
                        .build()))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOfSatisfying(InvalidRecipeChildIdException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                );
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
    }

    @Test
    void shouldRejectMovingRecipeIngredientBetweenSectionsWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntityWithTwoSections());
        var replacementIngredient = RecipeMother.updateRecipeIngredientRequestBuilder()
                .id(null)
                .build();
        var firstSection = RecipeMother.updateRecipeSectionRequestBuilder()
                .ingredients(List.of(replacementIngredient))
                .build();
        var secondSection = RecipeMother.updateRecipeSectionRequestBuilder()
                .id(RecipeTestConstants.SECOND_RECIPE_SECTION_ID)
                .ingredients(List.of(RecipeMother.updateRecipeIngredientRequestBuilder().build()))
                .build();
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .sections(List.of(firstSection, secondSection))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOfSatisfying(InvalidRecipeChildIdException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                );
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
        assertThat(recipe.getSections().getFirst().getIngredients().getFirst().getId())
                .isEqualTo(RecipeTestConstants.RECIPE_INGREDIENT_ID);
        assertThat(recipe.getSections().get(1).getIngredients().getFirst().getId())
                .isEqualTo(RecipeTestConstants.SECOND_RECIPE_INGREDIENT_ID);
    }

    @Test
    void shouldRejectUnknownIngredientChildIdWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntity());
        var request = RecipeMother.updateRecipeRequestBuilder()
                .name(UPDATED_NAME)
                .sections(List.of(RecipeMother.updateRecipeSectionRequestBuilder()
                        .ingredients(List.of(RecipeMother.updateRecipeIngredientRequestBuilder()
                                .id(RecipeTestConstants.MISSING_RECIPE_ID)
                                .build()))
                        .build()))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOf(InvalidRecipeChildIdException.class);
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
    }

    @Test
    void shouldRejectUpdateWhenRecipeDoesNotExist() {
        // given
        var request = RecipeMother.updateRecipeRequestBuilder().build();

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.MISSING_RECIPE_ID, request))
                .isInstanceOf(RecipeNotFoundException.class);
        then(ingredientFacade).shouldHaveNoInteractions();
    }

    @Test
    void shouldRejectUpdateWithMissingIngredientWithoutChangingRecipe() {
        // given
        var recipe = recipeRepository.save(RecipeMother.recipeEntity());
        var request = RecipeMother.updateRecipeRequestBuilder(RecipeTestConstants.MISSING_INGREDIENT_ID)
                .name(UPDATED_NAME)
                .build();
        willThrow(IngredientNotFoundException.byIngredientIds(Set.of(RecipeTestConstants.MISSING_INGREDIENT_ID)))
                .given(ingredientFacade)
                .validateIngredientsExist(Set.of(RecipeTestConstants.MISSING_INGREDIENT_ID));

        // when & then
        assertThatThrownBy(() -> recipeService.updateRecipe(RecipeTestConstants.RECIPE_ID, request))
                .isInstanceOf(IngredientNotFoundException.class);
        assertThat(recipe.getName()).isEqualTo(RecipeTestConstants.RECIPE_NAME);
    }

    @Test
    void shouldFindRecipeById() {
        // given
        recipeRepository.save(RecipeMother.recipeEntity());
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();

        // when
        var result = recipeService.findById(RecipeTestConstants.RECIPE_ID);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
    }

    @Test
    void shouldThrowWhenRecipeNotFound() {
        // given
        var expectedMessage = RecipeTestConstants.RECIPE_NOT_FOUND_MESSAGE;

        // when & then
        assertThatThrownBy(() -> recipeService.findById(RecipeTestConstants.MISSING_RECIPE_ID))
                .isInstanceOfSatisfying(RecipeNotFoundException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
    }

    @Test
    void shouldDeleteRecipeByIdempotently() {
        // given
        recipeRepository.save(RecipeMother.recipeEntity());

        // when & then
        assertThatCode(() -> {
            recipeService.deleteById(RecipeTestConstants.RECIPE_ID);
            recipeService.deleteById(RecipeTestConstants.RECIPE_ID);
        })
                .doesNotThrowAnyException();
        assertThat(recipeRepository.findAll()).isEmpty();
    }

    @Test
    void shouldIgnoreDeletingNonExistentRecipe() {
        // when & then
        assertThatCode(() -> recipeService.deleteById(RecipeTestConstants.MISSING_RECIPE_ID))
                .doesNotThrowAnyException();
        assertThat(recipeRepository.findAll()).isEmpty();
    }

    @Test
    void shouldReturnRecipes() {
        // given
        var recipeEntity = RecipeMother.recipeEntity();
        var expectedRecipe = RecipeMother.recipeSummaryDtoBuilder().build();
        var expectedPage = new PageImpl<>(List.of(expectedRecipe), DEFAULT_PAGEABLE, 1);
        recipeRepository.save(recipeEntity);

        // when
        var result = recipeService.getRecipes(DEFAULT_PAGEABLE);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedPage);
        assertThat(result.getSort()).containsExactly(
                Sort.Order.asc("name"),
                Sort.Order.asc("id")
        );
    }

    @Test
    void shouldRejectUnsupportedRecipeSortProperty() {
        // given
        var pageable = PageRequest.of(0, 10, Sort.by("description"));

        // when & then
        assertThatThrownBy(() -> recipeService.getRecipes(pageable))
                .isInstanceOf(InvalidSortPropertyException.class);
    }
}
