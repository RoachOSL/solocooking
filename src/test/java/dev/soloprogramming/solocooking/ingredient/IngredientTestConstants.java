/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class IngredientTestConstants {

    static final UUID INGREDIENT_ID = UUID.fromString("39f9be9f-88f2-4a50-bc83-97af6016c509");
    static final String INGREDIENT_NAME = "Eggs";
    static final String INGREDIENT_STORED_NAME = "eggs";
    static final Instant INGREDIENT_CREATED_AT = Instant.parse("2026-01-10T10:15:30Z");
    static final Instant INGREDIENT_UPDATED_AT = Instant.parse("2026-01-11T12:30:00Z");
}
