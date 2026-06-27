/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.Locale;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
interface IngredientMapper {

    IngredientDTO toDto(IngredientEntity ingredientEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "name", expression = "java(normalize(createIngredientRequest.name()))")
    IngredientEntity fromRequest(CreateIngredientRequest createIngredientRequest);

    @Named("normalize")
    default String normalize(String value) {
        return value.strip()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
