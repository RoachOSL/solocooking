/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.exception;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class BadRequestProblemDetail extends ProblemDetail {

    private static final String DEFAULT_VALIDATION_MESSAGE = "Invalid value";

    private final Map<String, String> errors;

    private BadRequestProblemDetail(Map<String, String> errors) {
        super(HttpStatus.BAD_REQUEST.value());
        setTitle("Validation failed");
        setDetail("Request validation failed.");
        this.errors = errors.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> Objects.requireNonNullElse(entry.getValue(), DEFAULT_VALIDATION_MESSAGE)
                ));
    }

    public static BadRequestProblemDetail forValidation(Map<String, String> errors) {
        return new BadRequestProblemDetail(errors);
    }

    @Schema(description = "Validation messages keyed by request field")
    public Map<String, String> getErrors() {
        return errors;
    }
}
