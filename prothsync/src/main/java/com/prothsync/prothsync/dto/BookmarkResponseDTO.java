package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크 토글 응답 DTO")
public record BookmarkResponseDTO(

    @Schema(description = "게시글 ID")
    Long postId,

    @Schema(description = "북마크 여부")
    boolean bookmarked
) {

    public static BookmarkResponseDTO of(Long postId, boolean bookmarked) {
        return new BookmarkResponseDTO(postId, bookmarked);
    }
}