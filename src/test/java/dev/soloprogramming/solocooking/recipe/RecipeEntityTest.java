/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeEntityTest {

    @Test
    void shouldPreserveSectionsWhenReplacingWithOwnView() {
        // given
        var recipe = RecipeMother.recipeEntity();
        var expectedSection = recipe.getSections().getFirst();

        // when
        recipe.replaceSections(recipe.getSections());

        // then
        assertThat(recipe.getSections()).containsExactly(expectedSection);
        assertThat(expectedSection.getPosition()).isZero();
    }

    @Test
    void shouldPreserveIngredientsWhenReplacingWithOwnView() {
        // given
        var section = RecipeMother.recipeEntity().getSections().getFirst();
        var expectedIngredient = section.getIngredients().getFirst();

        // when
        section.replaceIngredients(section.getIngredients());

        // then
        assertThat(section.getIngredients()).containsExactly(expectedIngredient);
        assertThat(expectedIngredient.getPosition()).isZero();
    }
}
