/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.time.Instant;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
class IngredientTestConstants {

    static final UUID INGREDIENT_ID = UUID.fromString("39f9be9f-88f2-4a50-bc83-97af6016c509");
    static final UUID MISSING_INGREDIENT_ID = UUID.fromString("af4733da-7ded-4a07-9f92-c8fd5d479b76");
    static final String INGREDIENT_NAME = "Eggs";
    static final String INGREDIENT_STORED_NAME = "eggs";
    static final String NORMALIZED_INGREDIENT_INPUT = "  Extra\tVirgin   Olive\nOil  ";
    static final String NORMALIZED_INGREDIENT_NAME = "extra virgin olive oil";
    static final String INGREDIENT_SEARCH_INPUT = "  EG  ";
    static final String SECOND_INGREDIENT_NAME = "milk";
    static final String THIRD_INGREDIENT_NAME = "flour";
    static final String DUPLICATED_INGREDIENT_INPUT = "  EGGS  ";
    static final String DUPLICATED_INGREDIENT_MESSAGE = "Ingredient [eggs] already exists.";
    static final String MISSING_INGREDIENT_MESSAGE =
            "Ingredient with id [%s] not found.".formatted(MISSING_INGREDIENT_ID);
    static final String MISSING_INGREDIENTS_MESSAGE =
            "Ingredients with ids [[%s]] not found.".formatted(MISSING_INGREDIENT_ID);
    static final Instant INGREDIENT_CREATED_AT = Instant.parse("2026-01-10T10:15:30Z");
    static final Instant INGREDIENT_UPDATED_AT = Instant.parse("2026-01-11T12:30:00Z");
}
