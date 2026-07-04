/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.IngredientTestFixtures;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeFacadeIT extends BaseIntegrationTest {

    @Autowired
    private RecipeFacade recipeFacade;

    @Autowired
    private IngredientFacade ingredientFacade;

    @Test
    void shouldCreateRecipe() {
        // given
        var ingredientId = givenExistingIngredientId();
        var request = createRecipeRequest(ingredientId);
        var expectedRecipe = expectedRecipe(ingredientId);

        // when
        var result = recipeFacade.createRecipe(request);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
    }

    @Test
    void shouldFindRecipeById() {
        // given
        var expectedRecipe = givenExistingRecipe();

        // when
        var result = recipeFacade.findById(expectedRecipe.id());

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
    }

    @Test
    void shouldReturnRecipesPage() {
        // given
        var recipe = givenExistingRecipe();
        var expectedRecipe = RecipeSummaryDTO.builder()
                .id(recipe.id())
                .name(recipe.name())
                .imageUrl(recipe.imageUrl())
                .description(recipe.description())
                .createdAt(recipe.createdAt())
                .updatedAt(recipe.updatedAt())
                .build();

        // when
        var result = recipeFacade.getRecipes(Pageable.unpaged());

        // then
        assertThat(result.getContent())
                .usingRecursiveFieldByFieldElementComparator(defaultRecursiveComparisonConfiguration())
                .containsExactly(expectedRecipe);
    }

    @Test
    void shouldDeleteRecipeById() {
        // given
        var recipe = givenExistingRecipe();

        // when
        recipeFacade.deleteById(recipe.id());

        // then
        assertThatThrownBy(() -> recipeFacade.findById(recipe.id()))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void shouldRejectRecipeWithMissingIngredient() {
        // given
        var request = createRecipeRequest(RecipeTestConstants.MISSING_INGREDIENT_ID);

        // when & then
        assertThatThrownBy(() -> recipeFacade.createRecipe(request))
                .isInstanceOfSatisfying(IngredientNotFoundException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(RecipeTestConstants.MISSING_INGREDIENTS_MESSAGE)
                );
        assertThat(recipeFacade.getRecipes(Pageable.unpaged())).isEmpty();
    }

    @Test
    void shouldThrowWhenRecipeDoesNotExist() {
        // when & then
        assertThatThrownBy(() -> recipeFacade.findById(RecipeTestConstants.MISSING_RECIPE_ID))
                .isInstanceOfSatisfying(RecipeNotFoundException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(RecipeTestConstants.RECIPE_NOT_FOUND_MESSAGE)
                );
    }

    private UUID givenExistingIngredientId() {
        return IngredientTestFixtures.givenExistingIngredient(ingredientFacade).id();
    }

    private RecipeDTO givenExistingRecipe() {
        return recipeFacade.createRecipe(createRecipeRequest(givenExistingIngredientId()));
    }

    private CreateRecipeRequest createRecipeRequest(UUID ingredientId) {
        return RecipeMother.createRecipeRequestBuilder(ingredientId)
                .build();
    }

    private RecipeDTO expectedRecipe(UUID ingredientId) {
        return RecipeMother.recipeDtoBuilder(ingredientId)
                .build();
    }
}
