package com.prothsync.prothsync.dto;

public record FollowResponseDTO(
    Long targetUserId,
    boolean followed,
    int followerCount
) {

    public static FollowResponseDTO of(Long targetUserId, boolean followed, int followerCount) {
        return new FollowResponseDTO(targetUserId, followed, followerCount);
    }
}
