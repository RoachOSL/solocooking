/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class CommonTestConstants {

    public static final String API_SERVLET_PATH = "/api";
    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
    public static final PageRequest DEFAULT_WEB_PAGE_REQUEST = PageRequest.of(0, 20);
    public static final PageRequest MAX_WEB_PAGE_REQUEST = PageRequest.of(0, 100);
    public static final String OVERSIZED_WEB_PAGE_SIZE = "200";
}
