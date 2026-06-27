/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeServiceTest {

    private static final UUID MISSING_INGREDIENT_ID = UUID.fromString("af4733da-7ded-4a07-9f92-c8fd5d479b76");

    private final InMemoryRecipeRepository recipeRepository = new InMemoryRecipeRepository();
    private final RecipeMapper recipeMapper = new RecipeMapperImpl();
    private final InMemoryIngredientFacade ingredientFacade = new InMemoryIngredientFacade();
    private final RecipeService recipeService = new RecipeService(recipeRepository, recipeMapper, ingredientFacade);

    @Test
    void shouldCreateRecipe() {
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        var expectedRecipeEntity = RecipeMother.recipeEntity();

        var result = recipeService.createRecipe(createRecipeRequest);

        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
        assertThat(recipeRepository.findAll())
                .singleElement()
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipeEntity);
    }

    @Test
    void shouldRejectRecipeWhenIngredientDoesNotExist() {
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder()
                .sections(List.of(RecipeMother.createRecipeSectionRequestBuilder()
                        .ingredients(List.of(RecipeMother.createRecipeIngredientRequestBuilder()
                                .ingredientId(MISSING_INGREDIENT_ID)
                                .build()))
                        .build()))
                .build();

        assertThatThrownBy(() -> recipeService.createRecipe(createRecipeRequest))
                .isInstanceOf(IngredientNotFoundException.class);
        assertThat(recipeRepository.findAll()).isEmpty();
    }

    @Test
    void shouldReturnRecipes() {
        var pageable = PageRequest.of(0, 10);
        var recipeEntity = RecipeMother.recipeEntity();
        var expectedRecipe = RecipeMother.recipeSummaryDtoBuilder().build();
        var expectedPage = new PageImpl<>(List.of(expectedRecipe), pageable, 1);
        recipeRepository.save(recipeEntity);

        var result = recipeService.getRecipes(pageable);

        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedPage);
    }
}
