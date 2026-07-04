/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import static org.assertj.core.api.Assertions.assertThat;

class EntityUuidGeneratorTest {

    private static final String BASE_PACKAGE = "dev.soloprogramming.solocooking";
    private static final String SINGLE_ID_FIELD_MESSAGE = "%s should have exactly one @Id field";
    private static final String GENERATED_VALUE_MESSAGE = "%s.%s should use @GeneratedValue";
    private static final String UUID_GENERATOR_MESSAGE = "%s.%s should use @UuidGenerator";
    private static final String CLASS_LOAD_FAILURE_MESSAGE = "Could not load entity class [%s].";

    @Test
    void shouldUseUuidVersion7GeneratorForAllEntityIds() {
        // given
        var entityClasses = findEntityClasses();

        // when & then
        assertThat(entityClasses).isNotEmpty();
        entityClasses.forEach(entityClass -> {
            var idFields = Arrays.stream(entityClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .toList();

            assertThat(idFields)
                    .as(SINGLE_ID_FIELD_MESSAGE, entityClass.getSimpleName())
                    .hasSize(1);

            var idField = idFields.getFirst();
            assertThat(idField.getAnnotation(GeneratedValue.class))
                    .as(GENERATED_VALUE_MESSAGE, entityClass.getSimpleName(), idField.getName())
                    .isNotNull();
            assertThat(idField.getAnnotation(UuidGenerator.class))
                    .as(UUID_GENERATOR_MESSAGE, entityClass.getSimpleName(), idField.getName())
                    .extracting(UuidGenerator::style)
                    .isEqualTo(UuidGenerator.Style.VERSION_7);
        });
    }

    private static List<? extends Class<?>> findEntityClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        return scanner.findCandidateComponents(BASE_PACKAGE).stream()
                .map(beanDefinition -> loadClass(beanDefinition.getBeanClassName()))
                .toList();
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(CLASS_LOAD_FAILURE_MESSAGE.formatted(className), exception);
        }
    }
}
