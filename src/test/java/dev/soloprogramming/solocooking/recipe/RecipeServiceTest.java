package dev.soloprogramming.solocooking.recipe;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
        assertThat(recipeRepository.findAll())
                .singleElement()
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
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
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedPage);
    }
}
