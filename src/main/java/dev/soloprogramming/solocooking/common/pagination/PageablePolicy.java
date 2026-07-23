/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.pagination;

import java.util.Set;

import dev.soloprogramming.solocooking.common.exception.InvalidSortPropertyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageablePolicy {

    private static final String TIE_BREAKER_PROPERTY = "id";

    private final Set<String> allowedSortProperties;
    private final Sort defaultSort;

    private PageablePolicy(Set<String> allowedSortProperties, Sort defaultSort) {
        this.allowedSortProperties = Set.copyOf(allowedSortProperties);
        this.defaultSort = defaultSort;
    }

    public static PageablePolicy of(Set<String> allowedSortProperties, Sort defaultSort) {
        return new PageablePolicy(allowedSortProperties, defaultSort);
    }

    public Pageable apply(Pageable pageable) {
        var requestedSort = pageable.getSort();
        validate(requestedSort);
        var effectiveSort = requestedSort.isSorted() ? withTieBreaker(requestedSort) : defaultSort;

        if (pageable.isUnpaged()) {
            return Pageable.unpaged(effectiveSort);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
    }

    private void validate(Sort sort) {
        sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !allowedSortProperties.contains(property))
                .findFirst()
                .ifPresent(property -> {
                    throw InvalidSortPropertyException.forProperty(property, allowedSortProperties);
                });
    }

    private Sort withTieBreaker(Sort sort) {
        var orders = sort.stream().toList();
        if (orders.stream().map(Sort.Order::getProperty).anyMatch(TIE_BREAKER_PROPERTY::equals)) {
            return sort;
        }

        var direction = orders.getLast().getDirection();
        return sort.and(Sort.by(direction, TIE_BREAKER_PROPERTY));
    }
}
