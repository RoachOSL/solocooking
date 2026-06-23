package dev.soloprogramming.solocooking.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestComparisonConfig {

    public static RecursiveComparisonConfiguration defaultRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("createdAt", "updatedAt")
                .build();
    }
}
