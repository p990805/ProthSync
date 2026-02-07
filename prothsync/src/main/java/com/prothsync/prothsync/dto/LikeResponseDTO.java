package com.prothsync.prothsync.dto;

public record LikeResponseDTO(
    Long postId,
    boolean liked,
    int likeCount
) {

    public static LikeResponseDTO of(Long postId, boolean liked, int likeCount) {
        return new LikeResponseDTO(postId, liked, likeCount);
    }
}
