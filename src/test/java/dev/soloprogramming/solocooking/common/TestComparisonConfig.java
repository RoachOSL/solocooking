/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

@UtilityClass
public class TestComparisonConfig {

    public static RecursiveComparisonConfiguration defaultRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("createdAt", "updatedAt")
                .build();
    }
}
