package com.prothsync.prothsync.global;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> data,
    PageableResponse pageable
) {

    public static <T> PageResponse<T> of(List<T> data, Page<?> page) {
        return new PageResponse<>(
            data,
            PageableResponse.from(page)
        );
    }

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(
            List.of(),
            new PageableResponse(0, 0, 0, 0, false, true, true)
        );
    }
}