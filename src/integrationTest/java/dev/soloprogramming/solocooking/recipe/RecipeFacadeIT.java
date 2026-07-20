/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.IngredientTestFixtures;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeSectionRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.soloprogramming.solocooking.common.TestComparisonConfig.defaultRecursiveComparisonConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeFacadeIT extends BaseIntegrationTest {

    private static final String MAX_RECIPE_NAME = "r".repeat(255);
    private static final String MAX_RECIPE_IMAGE_URL = "https://" + "i".repeat(2040);
    private static final String MAX_RECIPE_DESCRIPTION = "d".repeat(5000);
    private static final String MAX_RECIPE_SECTION_NAME = "s".repeat(255);
    private static final String MAX_RECIPE_INGREDIENT_UNIT = "u".repeat(64);
    private static final String MAX_RECIPE_INGREDIENT_NOTE = "n".repeat(500);

    @Autowired
    private RecipeFacade recipeFacade;

    @Autowired
    private IngredientFacade ingredientFacade;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateRecipe() {
        // given
        var ingredientId = givenExistingIngredientId();
        var request = RecipeMother.createRecipeRequestBuilder(ingredientId)
                .build();
        var expectedRecipe = RecipeMother.recipeDtoBuilder(ingredientId)
                .build();

        // when
        var result = recipeFacade.createRecipe(request);

        // then
        assertThat(result)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(expectedRecipe);
    }

    @Test
    void shouldPersistMaximumRecipeFieldLengths() {
        // given
        var ingredientId = givenExistingIngredientId();
        var ingredientRequest = RecipeMother.createRecipeIngredientRequestBuilder(ingredientId)
                .unit(MAX_RECIPE_INGREDIENT_UNIT)
                .note(MAX_RECIPE_INGREDIENT_NOTE)
                .build();
        var sectionRequest = RecipeMother.createRecipeSectionRequestBuilder(ingredientId)
                .name(MAX_RECIPE_SECTION_NAME)
                .ingredients(List.of(ingredientRequest))
                .build();
        var request = RecipeMother.createRecipeRequestBuilder(ingredientId)
                .name(MAX_RECIPE_NAME)
                .imageUrl(MAX_RECIPE_IMAGE_URL)
                .description(MAX_RECIPE_DESCRIPTION)
                .sections(List.of(sectionRequest))
                .build();

        // when
        var createdRecipe = recipeFacade.createRecipe(request);
        var persistedRecipe = recipeFacade.findById(createdRecipe.id());

        // then
        assertThat(persistedRecipe)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(createdRecipe);
    }

    @Test
    void shouldUpdateAndPersistEntireRecipeAggregate() {
        // given
        var ingredientId = givenExistingIngredientId();
        var initialSection = RecipeMother.createRecipeSectionRequestBuilder(ingredientId).build();
        var createdRecipeId = recipeFacade.createRecipe(RecipeMother.createRecipeRequestBuilder(ingredientId)
                .sections(List.of(initialSection, initialSection))
                .build()).id();
        var originalRecipe = recipeFacade.findById(createdRecipeId);
        var removedSection = originalRecipe.sections().getFirst();
        var retainedSection = originalRecipe.sections().get(1);
        var retainedIngredient = retainedSection.ingredients().getFirst();
        var newIngredient = UpdateRecipeIngredientRequest.builder()
                .ingredientId(ingredientId)
                .amount(BigDecimal.ONE)
                .unit("piece")
                .note(null)
                .build();
        var updatedIngredient = UpdateRecipeIngredientRequest.builder()
                .id(retainedIngredient.id())
                .ingredientId(ingredientId)
                .amount(BigDecimal.TEN)
                .unit("grams")
                .note("updated")
                .build();
        var updatedSection = UpdateRecipeSectionRequest.builder()
                .id(retainedSection.id())
                .name("Retained section")
                .ingredients(List.of(newIngredient, updatedIngredient))
                .build();
        var addedSection = UpdateRecipeSectionRequest.builder()
                .name("Added section")
                .ingredients(List.of(newIngredient))
                .build();
        var request = UpdateRecipeRequest.builder()
                .name("Updated recipe")
                .imageUrl("https://example.com/updated.jpg")
                .description("Updated description")
                .sections(List.of(updatedSection, addedSection))
                .build();

        // when
        var updatedRecipe = recipeFacade.updateRecipe(originalRecipe.id(), request);
        var persistedRecipe = recipeFacade.findById(originalRecipe.id());

        // then
        assertThat(persistedRecipe)
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(updatedRecipe);
        assertThat(updatedRecipe.createdAt()).isEqualTo(originalRecipe.createdAt());
        assertThat(updatedRecipe.sections()).hasSize(2);
        assertThat(updatedRecipe.sections().getFirst().id()).isEqualTo(retainedSection.id());
        assertThat(updatedRecipe.sections().getFirst().position()).isZero();
        assertThat(updatedRecipe.sections().get(1).id()).isNotNull();
        assertThat(updatedRecipe.sections().get(1).position()).isEqualTo(1);
        var newIngredientId = updatedRecipe.sections().getFirst().ingredients().getFirst().id();
        var addedSectionIngredientId = updatedRecipe.sections().get(1).ingredients().getFirst().id();
        assertThat(newIngredientId).isNotNull().isNotEqualTo(retainedIngredient.id());
        assertThat(addedSectionIngredientId)
                .isNotNull()
                .isNotEqualTo(retainedIngredient.id())
                .isNotEqualTo(newIngredientId);
        assertThat(updatedRecipe.sections().getFirst().ingredients())
                .extracting(ingredient -> ingredient.id(), ingredient -> ingredient.position())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(newIngredientId, 0),
                        org.assertj.core.groups.Tuple.tuple(retainedIngredient.id(), 1)
                );
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM recipe_section WHERE id = ?",
                Integer.class,
                removedSection.id()
        )).isZero();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM recipe_ingredient WHERE id = ?",
                Integer.class,
                removedSection.ingredients().getFirst().id()
        )).isZero();
    }

    @Test
    void shouldRollbackUpdateWhenIngredientDoesNotExist() {
        // given
        var createdRecipe = givenExistingRecipe();
        var section = createdRecipe.sections().getFirst();
        var ingredient = section.ingredients().getFirst();
        var request = RecipeMother.updateRecipeRequestBuilder(RecipeTestConstants.MISSING_INGREDIENT_ID)
                .name("Should not persist")
                .sections(List.of(RecipeMother.updateRecipeSectionRequestBuilder(RecipeTestConstants.MISSING_INGREDIENT_ID)
                        .id(section.id())
                        .ingredients(List.of(RecipeMother.updateRecipeIngredientRequestBuilder(
                                        RecipeTestConstants.MISSING_INGREDIENT_ID)
                                .id(ingredient.id())
                                .build()))
                        .build()))
                .build();

        // when & then
        assertThatThrownBy(() -> recipeFacade.updateRecipe(createdRecipe.id(), request))
                .isInstanceOf(IngredientNotFoundException.class);
        assertThat(recipeFacade.findById(createdRecipe.id()))
                .usingRecursiveComparison(defaultRecursiveComparisonConfiguration())
                .isEqualTo(createdRecipe);
    }

    @Test
    void shouldRejectUpdatingMissingRecipe() {
        // given
        var request = RecipeMother.updateRecipeRequestBuilder().build();

        // when & then
        assertThatThrownBy(() -> recipeFacade.updateRecipe(RecipeTestConstants.MISSING_RECIPE_ID, request))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "999999999.999"})
    void shouldPersistSupportedIngredientAmount(String amountValue) {
        // given
        var amount = new BigDecimal(amountValue);
        var request = createRecipeRequestWithAmount(givenExistingIngredientId(), amount);

        // when
        var createdRecipe = recipeFacade.createRecipe(request);
        var persistedRecipe = recipeFacade.findById(createdRecipe.id());

        // then
        assertThat(persistedRecipe.sections().getFirst().ingredients().getFirst().amount())
                .isEqualByComparingTo(amount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0001", "1000000000"})
    void shouldRejectUnsupportedIngredientAmount(String amountValue) {
        // given
        var request = createRecipeRequestWithAmount(
                givenExistingIngredientId(),
                new BigDecimal(amountValue)
        );

        // when & then
        assertThatThrownBy(() -> recipeFacade.createRecipe(request))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldRejectNonPositiveIngredientAmountAtDatabaseBoundary() {
        // given
        var recipe = givenExistingRecipe();
        var recipeIngredientId = recipe.sections().getFirst().ingredients().getFirst().id();

        // when & then
        assertThatThrownBy(() -> jdbcTemplate.update(
                "UPDATE recipe_ingredient SET amount = 0 WHERE id = ?",
                recipeIngredientId
        )).isInstanceOf(DataIntegrityViolationException.class);
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
    void shouldDeleteRecipeByIdempotently() {
        // given
        var recipe = givenExistingRecipe();

        // when
        recipeFacade.deleteById(recipe.id());
        recipeFacade.deleteById(recipe.id());

        // then
        assertThatThrownBy(() -> recipeFacade.findById(recipe.id()))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void shouldRejectRecipeWithMissingIngredient() {
        // given
        var request = RecipeMother.createRecipeRequestBuilder(RecipeTestConstants.MISSING_INGREDIENT_ID)
                .build();

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

    private CreateRecipeRequest createRecipeRequestWithAmount(UUID ingredientId, BigDecimal amount) {
        var ingredientRequest = RecipeMother.createRecipeIngredientRequestBuilder(ingredientId)
                .amount(amount)
                .build();
        var sectionRequest = RecipeMother.createRecipeSectionRequestBuilder(ingredientId)
                .ingredients(List.of(ingredientRequest))
                .build();
        return RecipeMother.createRecipeRequestBuilder(ingredientId)
                .sections(List.of(sectionRequest))
                .build();
    }

    private UUID givenExistingIngredientId() {
        return IngredientTestFixtures.givenExistingIngredient(ingredientFacade).id();
    }

    private RecipeDTO givenExistingRecipe() {
        return recipeFacade.createRecipe(RecipeMother.createRecipeRequestBuilder(givenExistingIngredientId())
                .build());
    }
}
