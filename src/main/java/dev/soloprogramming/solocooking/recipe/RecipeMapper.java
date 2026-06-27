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
                .sections(toOrderedSectionDtos(recipeEntity.getSections()))
                .createdAt(recipeEntity.getCreatedAt())
                .updatedAt(recipeEntity.getUpdatedAt())
                .build();
    }

    RecipeSummaryDTO toSummaryDto(RecipeEntity recipeEntity);

    private List<RecipeSectionDTO> toOrderedSectionDtos(List<RecipeSectionEntity> sections) {
        if (sections == null) {
            return List.of();
        }

        return sections.stream()
                .sorted(Comparator.comparing(RecipeSectionEntity::getPosition))
                .map(this::toSectionDto)
                .toList();
    }

    private RecipeSectionDTO toSectionDto(RecipeSectionEntity section) {
        return RecipeSectionDTO.builder()
                .id(section.getId())
                .name(section.getName())
                .position(section.getPosition())
                .ingredients(toOrderedIngredientDtos(section.getIngredients()))
                .build();
    }

    private List<RecipeIngredientDTO> toOrderedIngredientDtos(List<RecipeIngredientEntity> ingredients) {
        if (ingredients == null) {
            return List.of();
        }

        return ingredients.stream()
                .sorted(Comparator.comparing(RecipeIngredientEntity::getPosition))
                .map(this::toIngredientDto)
                .toList();
    }

    private RecipeIngredientDTO toIngredientDto(RecipeIngredientEntity ingredient) {
        return RecipeIngredientDTO.builder()
                .id(ingredient.getId())
                .ingredientId(ingredient.getIngredientId())
                .amount(ingredient.getAmount())
                .unit(ingredient.getUnit())
                .note(ingredient.getNote())
                .position(ingredient.getPosition())
                .build();
    }
}
