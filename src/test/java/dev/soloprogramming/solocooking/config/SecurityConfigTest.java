/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.config;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void shouldAllowCorsRequestsWithoutCredentials() throws IOException {
        // given
        var request = new MockHttpServletRequest(HttpMethod.OPTIONS.name(), "/api/recipes");
        request.addHeader(HttpHeaders.ORIGIN, "https://example.com");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name());
        var response = new MockHttpServletResponse();
        var configuration = new SecurityConfig()
                .corsConfigurationSource()
                .getCorsConfiguration(request);

        // when
        var allowed = new DefaultCorsProcessor().processRequest(configuration, request, response);

        // then
        assertThat(allowed).isTrue();
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("*");
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isNull();
    }
}
