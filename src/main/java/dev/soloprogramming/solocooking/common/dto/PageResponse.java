/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(requiredProperties = {"content", "page"})
public record PageResponse<T>(List<T> content, PageMetadata page) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new PageMetadata(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    @Schema(requiredProperties = {"number", "size", "totalElements", "totalPages"})
    public record PageMetadata(int number, int size, long totalElements, int totalPages) {
    }
}
