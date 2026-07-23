/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.exception;

import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.soloprogramming.solocooking.common.CommonTestConstants.API_SERVLET_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(GlobalExceptionHandlerTest.ValidationController.class)
@Import(GlobalExceptionHandlerTest.ValidationController.class)
class GlobalExceptionHandlerTest {

    private static final String VALIDATION_ENDPOINT = API_SERVLET_PATH + "/test-validation";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldPreserveInternalServerErrorForInvalidReturnValue() {
        // when & then
        assertThat(get("/invalid-return-value"))
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldUseAliasedRequestParameterNameInValidationError() {
        // when & then
        assertThat(get("/aliased-parameter")
                .param("query", " "))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors.query")
                .isEqualTo("must not be blank");
    }

    private MockMvcTester.MockMvcRequestBuilder get(String path) {
        return mockMvcTester.get()
                .uri(VALIDATION_ENDPOINT + path)
                .servletPath(API_SERVLET_PATH);
    }

    @RestController
    static class ValidationController {

        @GetMapping("/test-validation/invalid-return-value")
        @NotBlank
        String invalidReturnValue() {
            return " ";
        }

        @GetMapping("/test-validation/aliased-parameter")
        String aliasedParameter(@RequestParam("query") @NotBlank String internalName) {
            return internalName;
        }
    }
}
