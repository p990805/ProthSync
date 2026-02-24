package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import java.time.LocalDateTime;

public record BlockedUserResponseDTO(
    Long userId,
    String nickName,
    UserType userType,
    String profileImageUrl,
    LocalDateTime blockedAt
) {
    public static BlockedUserResponseDTO of(User user, LocalDateTime blockedAt) {
        return new BlockedUserResponseDTO(
            user.getUserId(),
            user.getNickName(),
            user.getUserType(),
            user.getProfileImageUrl(),
            blockedAt
        );
    }
}
