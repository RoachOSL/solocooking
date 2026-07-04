/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

@UtilityClass
public class TestComparisonConfig {

    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String ID_FIELD = "id";
    private static final String NESTED_ID_FIELD_REGEX = ".*\\.id";

    public static RecursiveComparisonConfiguration defaultRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration.builder()
                .withIgnoredFields(CREATED_AT_FIELD, UPDATED_AT_FIELD, ID_FIELD)
                .withIgnoredFieldsMatchingRegexes(NESTED_ID_FIELD_REGEX)
                .build();
    }
}
