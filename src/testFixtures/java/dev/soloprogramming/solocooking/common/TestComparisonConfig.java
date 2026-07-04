/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import java.math.BigDecimal;
import java.util.Comparator;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

@UtilityClass
public class TestComparisonConfig {

    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String UPDATED_AT_FIELD = "updatedAt";
    private static final String ID_FIELD = "id";
    private static final String NESTED_ID_FIELD_REGEX = ".*\\.id";
    private static final Comparator<BigDecimal> BIG_DECIMAL_COMPARATOR = BigDecimal::compareTo;

    public static RecursiveComparisonConfiguration defaultRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration.builder()
                .withIgnoredFields(CREATED_AT_FIELD, UPDATED_AT_FIELD, ID_FIELD)
                .withIgnoredFieldsMatchingRegexes(NESTED_ID_FIELD_REGEX)
                .withComparatorForType(BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .build();
    }
}
