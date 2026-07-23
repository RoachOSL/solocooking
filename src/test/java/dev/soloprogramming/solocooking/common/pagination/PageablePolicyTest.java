/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.pagination;

import java.util.Set;

import dev.soloprogramming.solocooking.common.exception.InvalidSortPropertyException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageablePolicyTest {

    private static final PageablePolicy POLICY = PageablePolicy.of(
            Set.of("id", "name"),
            Sort.by(Sort.Order.asc("name"), Sort.Order.asc("id"))
    );

    @Test
    void shouldApplyStableDefaultSort() {
        // when
        var result = POLICY.apply(PageRequest.of(2, 10));

        // then
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort()).containsExactly(
                Sort.Order.asc("name"),
                Sort.Order.asc("id")
        );
    }

    @Test
    void shouldAppendTieBreakerUsingRequestedDirection() {
        // given
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("name")));

        // when
        var result = POLICY.apply(pageable);

        // then
        assertThat(result.getSort()).containsExactly(
                Sort.Order.desc("name"),
                Sort.Order.desc("id")
        );
    }

    @Test
    void shouldNotDuplicateExplicitTieBreaker() {
        // given
        var pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Order.asc("name"), Sort.Order.desc("id"))
        );

        // when
        var result = POLICY.apply(pageable);

        // then
        assertThat(result.getSort()).containsExactly(
                Sort.Order.asc("name"),
                Sort.Order.desc("id")
        );
    }

    @Test
    void shouldApplyDefaultSortToUnpagedRequest() {
        // when
        var result = POLICY.apply(Pageable.unpaged());

        // then
        assertThat(result.isUnpaged()).isTrue();
        assertThat(result.getSort()).containsExactly(
                Sort.Order.asc("name"),
                Sort.Order.asc("id")
        );
    }

    @Test
    void shouldRejectUnsupportedSortProperty() {
        // given
        var pageable = PageRequest.of(0, 20, Sort.by("description"));

        // when & then
        assertThatThrownBy(() -> POLICY.apply(pageable))
                .isInstanceOfSatisfying(InvalidSortPropertyException.class, exception ->
                        assertThat(exception.getMessage())
                                .isEqualTo("unsupported sort property 'description'; allowed: id, name")
                );
    }
}
