package com.prothsync.prothsync.global;

import org.springframework.data.domain.Page;

public record PageableResponse(
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean isFirst,
    boolean isLast
) {

    public static PageableResponse from(Page<?> page) {
        return new PageableResponse(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.isFirst(),
            page.isLast()
        );
    }
}
