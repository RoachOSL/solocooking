/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BadRequestProblemDetailTest {

    @Test
    void shouldUseFallbackWhenValidationMessageIsMissing() {
        // given
        var errors = new HashMap<String, String>();
        errors.put("name", null);

        // when
        var problemDetail = BadRequestProblemDetail.forValidation(errors);

        // then
        assertThat(problemDetail.getErrors())
                .containsExactlyEntriesOf(Map.of("name", "Invalid value"));
    }
}
