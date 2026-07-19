/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;
import java.util.Set;

import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
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
    }
}
