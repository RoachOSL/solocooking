/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import dev.soloprogramming.solocooking.common.InMemoryRepository;

final class InMemoryRecipeRepository extends InMemoryRepository<RecipeEntity, UUID>
        implements RecipeRepository {

    private int recipeIdSequence;
    private int sectionIdSequence;
    private int ingredientIdSequence;

    @Override
    public <S extends RecipeEntity> S save(S recipeEntity) {
        fillChildIds(recipeEntity);
        return super.save(recipeEntity);
    }

    @Override
    public Optional<RecipeEntity> findByIdWithoutDetails(UUID recipeId) {
        return findById(recipeId);
    }

    @Override
    protected UUID getId(RecipeEntity recipeEntity) {
        return recipeEntity.getId();
    }

    @Override
    protected void setId(RecipeEntity recipeEntity, UUID recipeId) {
        recipeEntity.setId(recipeId);
    }

    @Override
    protected UUID generateId() {
        return nextRecipeId();
    }

    private void fillChildIds(RecipeEntity recipeEntity) {
        recipeEntity.getSections().forEach(section -> {
            if (section.getId() == null) {
                section.setId(nextId(
                        RecipeTestConstants.RECIPE_SECTION_ID,
                        this::nextSectionGeneratedId,
                        usedSectionIds(recipeEntity)
                ));
            }
            section.getIngredients().forEach(ingredient -> {
                if (ingredient.getId() == null) {
                    ingredient.setId(nextId(
                            RecipeTestConstants.RECIPE_INGREDIENT_ID,
                            this::nextIngredientGeneratedId,
                            usedIngredientIds(recipeEntity)
                    ));
                }
            });
        });
    }

    private UUID nextRecipeId() {
        if (!existsById(RecipeTestConstants.RECIPE_ID)) {
            return RecipeTestConstants.RECIPE_ID;
        }

        UUID recipeId;
        do {
            recipeId = generatedId("recipe", recipeIdSequence++);
        } while (existsById(recipeId));

        return recipeId;
    }

    private UUID nextId(UUID firstId, Supplier<UUID> generatedId, HashSet<UUID> usedIds) {
        if (!usedIds.contains(firstId)) {
            return firstId;
        }

        UUID id;
        do {
            id = generatedId.get();
        } while (usedIds.contains(id));

        return id;
    }

    private UUID nextSectionGeneratedId() {
        return generatedId("recipe-section", sectionIdSequence++);
    }

    private UUID nextIngredientGeneratedId() {
        return generatedId("recipe-ingredient", ingredientIdSequence++);
    }

    private UUID generatedId(String idPrefix, int index) {
        return UUID.nameUUIDFromBytes("%s-%d".formatted(idPrefix, index).getBytes(StandardCharsets.UTF_8));
    }

    private HashSet<UUID> usedSectionIds(RecipeEntity recipeEntity) {
        var usedIds = findAll().stream()
                .flatMap(recipe -> recipe.getSections().stream())
                .map(RecipeSectionEntity::getId)
                .filter(Objects::nonNull)
                .collect(HashSet<UUID>::new, HashSet::add, HashSet::addAll);

        recipeEntity.getSections().stream()
                .map(RecipeSectionEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);

        return usedIds;
    }

    private HashSet<UUID> usedIngredientIds(RecipeEntity recipeEntity) {
        var usedIds = findAll().stream()
                .flatMap(recipe -> recipe.getSections().stream())
                .flatMap(section -> section.getIngredients().stream())
                .map(RecipeIngredientEntity::getId)
                .filter(Objects::nonNull)
                .collect(HashSet<UUID>::new, HashSet::add, HashSet::addAll);

        recipeEntity.getSections().stream()
                .flatMap(section -> section.getIngredients().stream())
                .map(RecipeIngredientEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);

        return usedIds;
    }
}
