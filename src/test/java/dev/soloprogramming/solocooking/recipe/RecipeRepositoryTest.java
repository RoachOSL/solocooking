/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecipeFactory recipeFactory;

    @Test
    void shouldFindRecipeWithSectionsAndIngredients() {
        // given
        var recipe = recipeFactory.from(RecipeMother.createRecipeRequestBuilder().build());
        var savedRecipe = recipeRepository.saveAndFlush(recipe);
        entityManager.clear();

        // when
        var result = recipeRepository.findById(savedRecipe.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getSections())
                .singleElement()
                .satisfies(section -> assertThat(section.getIngredients())
                        .singleElement()
                        .satisfies(ingredient -> assertThat(ingredient.getIngredientId())
                                .isEqualTo(RecipeTestConstants.INGREDIENT_ID)));
    }
}
