package com.prothsync.prothsync.dto;

public record BlockResponseDTO(
    Long targetUserId,
    boolean blocked
) {
    public static BlockResponseDTO of(Long targetUserId, boolean blocked) {
        return new BlockResponseDTO(targetUserId, blocked);
    }
}
