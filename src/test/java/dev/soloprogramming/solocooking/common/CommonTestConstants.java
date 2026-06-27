/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class CommonTestConstants {

    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
}
