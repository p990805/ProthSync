package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserRole;
import com.prothsync.prothsync.entity.user.UserType;
import java.time.LocalDateTime;

public record AdminUserResponseDTO(
    Long userId,
    String userName,
    String nickName,
    String email,
    UserType userType,
    UserRole userRole,
    boolean suspended,
    LocalDateTime suspendedAt,
    LocalDateTime suspendedUntil,
    String suspendReason,
    LocalDateTime createdAt
) {

    public static AdminUserResponseDTO from(User user) {
        return new AdminUserResponseDTO(
            user.getUserId(),
            user.getUserName(),
            user.getNickName(),
            user.getEmail(),
            user.getUserType(),
            user.getUserRole(),
            user.isSuspended(),
            user.getSuspendedAt(),
            user.getSuspendedUntil(),
            user.getSuspendReason(),
            user.getCreatedAt()
        );
    }
}