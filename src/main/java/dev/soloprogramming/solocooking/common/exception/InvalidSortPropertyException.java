/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.exception;

import java.util.Collection;
import java.util.stream.Collectors;

public final class InvalidSortPropertyException extends RuntimeException {

    private InvalidSortPropertyException(String property, Collection<String> allowedProperties) {
        super("unsupported sort property '%s'; allowed: %s".formatted(
                property,
                allowedProperties.stream().sorted().collect(Collectors.joining(", "))
        ));
    }

    public static InvalidSortPropertyException forProperty(
            String property,
            Collection<String> allowedProperties
    ) {
        return new InvalidSortPropertyException(property, allowedProperties);
    }
}
