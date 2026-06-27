/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RecipeTestConstants {

    static final UUID RECIPE_ID = UUID.fromString("2b1c02d4-dc91-4f78-a4b3-e342b492bc25");
    static final UUID RECIPE_SECTION_ID = UUID.fromString("bbf025ab-fcf7-466a-b939-d924831f3487");
    static final UUID RECIPE_INGREDIENT_ID = UUID.fromString("4fbd5a73-8f33-4ac7-8a75-9f78d78b3594");
    static final UUID INGREDIENT_ID = UUID.fromString("39f9be9f-88f2-4a50-bc83-97af6016c509");
    static final UUID MISSING_INGREDIENT_ID = UUID.fromString("af4733da-7ded-4a07-9f92-c8fd5d479b76");
    static final String RECIPE_NAME = "Pasta carbonara";
    static final String RECIPE_IMAGE_URL = "https://example.com/carbonara.jpg";
    static final String RECIPE_DESCRIPTION = "Classic pasta carbonara";
    static final String RECIPE_SECTION_NAME = "Main";
    static final int RECIPE_SECTION_POSITION = 0;
    static final BigDecimal RECIPE_INGREDIENT_AMOUNT = BigDecimal.valueOf(2);
    static final String RECIPE_INGREDIENT_UNIT = "pcs";
    static final String RECIPE_INGREDIENT_NOTE = "large eggs";
    static final int RECIPE_INGREDIENT_POSITION = 0;
    static final String MISSING_INGREDIENTS_MESSAGE =
            "Ingredients with ids [[%s]] not found.".formatted(MISSING_INGREDIENT_ID);
    static final Instant RECIPE_CREATED_AT = Instant.parse("2026-01-10T10:15:30Z");
    static final Instant RECIPE_UPDATED_AT = Instant.parse("2026-01-11T12:30:00Z");
}
