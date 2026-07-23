/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.Locale;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
class IngredientNameNormalizer {

    private static final Pattern UNICODE_WHITESPACE = Pattern.compile(
            "\\s+",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    static String normalize(String value) {
        var lowercasedValue = value.toLowerCase(Locale.ROOT);
        return UNICODE_WHITESPACE.matcher(lowercasedValue)
                .replaceAll(" ")
                .strip();
    }
}
