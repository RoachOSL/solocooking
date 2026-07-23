/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = IngredientNameValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT,
        ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIngredientName {

    String message() default "invalid ingredient name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean required() default true;
}

final class IngredientNameValidator implements ConstraintValidator<ValidIngredientName, String> {

    private static final int MAX_LENGTH = 255;
    private static final String BLANK_MESSAGE = "must not be blank";
    private static final String SIZE_MESSAGE = "size must be between 1 and 255";

    private boolean required;

    @Override
    public void initialize(ValidIngredientName constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            if (required) {
                return violation(context, BLANK_MESSAGE);
            }
            return true;
        }
        if (codePointLength(value) > MAX_LENGTH) {
            return violation(context, SIZE_MESSAGE);
        }

        var normalizedValue = IngredientNameNormalizer.normalize(value);
        if (normalizedValue.isEmpty()) {
            return violation(context, BLANK_MESSAGE);
        }
        if (codePointLength(normalizedValue) > MAX_LENGTH) {
            return violation(context, SIZE_MESSAGE);
        }
        return true;
    }

    private boolean violation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }

    private int codePointLength(String value) {
        return value.codePointCount(0, value.length());
    }
}
