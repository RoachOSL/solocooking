package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeServiceTest {

    private final InMemoryRecipeRepository recipeRepository = new InMemoryRecipeRepository();
    private final RecipeMapper recipeMapper = new RecipeMapperImpl();
    private final RecipeService recipeService = new RecipeService(recipeRepository, recipeMapper);

    @Test
    void shouldCreateRecipe() {
        var createRecipeRequest = RecipeMother.createRecipeRequestBuilder().build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        var expectedRecipeEntity = RecipeMother.recipeEntity();

        var result = recipeService.createRecipe(createRecipeRequest);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedRecipe);
        assertThat(recipeRepository.findAll())
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(expectedRecipeEntity);
    }

    @Test
    void shouldReturnRecipes() {
        var pageable = PageRequest.of(0, 10);
        var recipeEntity = RecipeMother.recipeEntity();
        var expectedRecipe = RecipeMother.recipeDtoBuilder().build();
        var expectedPage = new PageImpl<>(List.of(expectedRecipe), pageable, 1);
        recipeRepository.save(recipeEntity);

        var result = recipeService.getRecipes(pageable);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedPage);
    }
}
