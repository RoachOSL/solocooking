/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.DEFAULT_PAGEABLE;
import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

class RecipeServiceTest {

    private static final UUID MISSING_INGREDIENT_ID = UUID.fromString("af4733da-7ded-4a07-9f92-c8fd5d479b76");

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
                                .ingredientId(MISSING_INGREDIENT_ID)
                                .build()))
                        .build()))
                .build();
        willThrow(IngredientNotFoundException.byIngredientIds(Set.of(MISSING_INGREDIENT_ID)))
                .given(ingredientFacade)
                .validateIngredientsExist(Set.of(MISSING_INGREDIENT_ID));
        var expectedMessage = "Ingredients with ids [[%s]] not found.".formatted(MISSING_INGREDIENT_ID);

        // when & then
        assertThatThrownBy(() -> recipeService.createRecipe(createRecipeRequest))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getBody().getDetail()).isEqualTo(expectedMessage);
                });
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
