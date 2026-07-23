/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return handleValidationException(ex, errors, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        if (ex.isForReturnValue()) {
            return super.handleHandlerMethodValidationException(ex, headers, status, request);
        }

        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            var parameterName = requestParameterName(result.getMethodParameter());
            result.getResolvableErrors().stream()
                    .findFirst()
                    .ifPresent(error -> errors.put(parameterName, error.getDefaultMessage()));
        });

        return handleValidationException(ex, errors, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return handleValidationException(
                ex,
                Map.of(ex.getParameterName(), "is required"),
                headers,
                request
        );
    }

    @ExceptionHandler(InvalidSortPropertyException.class)
    protected ResponseEntity<Object> handleInvalidSortPropertyException(
            InvalidSortPropertyException ex,
            WebRequest request
    ) {
        return handleValidationException(
                ex,
                Map.of("sort", ex.getMessage()),
                new HttpHeaders(),
                request
        );
    }

    private ResponseEntity<Object> handleValidationException(
            Exception ex,
            Map<String, String> errors,
            HttpHeaders headers,
            WebRequest request
    ) {
        var body = BadRequestProblemDetail.forValidation(errors);

        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    private String requestParameterName(org.springframework.core.MethodParameter methodParameter) {
        var requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            var declaredName = requestParam.name().isBlank() ? requestParam.value() : requestParam.name();
            if (!declaredName.isBlank()) {
                return declaredName;
            }
        }
        return Objects.requireNonNullElse(methodParameter.getParameterName(), "request");
    }

    @ExceptionHandler(ErrorDetails.class)
    protected ResponseEntity<Object> handleGenericException(ErrorDetails ex, WebRequest request) {
        var body = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getHttpStatus(), request);
    }
}
