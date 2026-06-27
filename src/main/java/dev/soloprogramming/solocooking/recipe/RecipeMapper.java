/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.Comparator;
import java.util.List;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeIngredientDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSectionDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import org.mapstruct.Mapper;

@Mapper
interface RecipeMapper {

    default RecipeDTO toDto(RecipeEntity recipeEntity) {
        if (recipeEntity == null) {
            return null;
        }

        return RecipeDTO.builder()
                .id(recipeEntity.getId())
                .name(recipeEntity.getName())
                .imageUrl(recipeEntity.getImageUrl())
                .description(recipeEntity.getDescription())
                .sections(orderedSections(recipeEntity.getSections()))
                .createdAt(recipeEntity.getCreatedAt())
                .updatedAt(recipeEntity.getUpdatedAt())
                .build();
    }

    RecipeSummaryDTO toSummaryDto(RecipeEntity recipeEntity);

    default RecipeSectionDTO toSectionDto(RecipeSectionEntity section) {
        return RecipeSectionDTO.builder()
                .id(section.getId())
                .name(section.getName())
                .position(section.getPosition())
                .ingredients(orderedIngredients(section.getIngredients()))
                .build();
    }

    RecipeIngredientDTO toIngredientDto(RecipeIngredientEntity ingredient);

    default List<RecipeSectionDTO> orderedSections(List<RecipeSectionEntity> sections) {
        return sections.stream()
                .sorted(Comparator.comparing(RecipeSectionEntity::getPosition))
                .map(this::toSectionDto)
                .toList();
    }

    default List<RecipeIngredientDTO> orderedIngredients(List<RecipeIngredientEntity> ingredients) {
        return ingredients.stream()
                .sorted(Comparator.comparing(RecipeIngredientEntity::getPosition))
                .map(this::toIngredientDto)
                .toList();
    }
}
