package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;

public record FollowUserResponseDTO(
    Long userId,
    String nickName,
    UserType userType
) {

    public static FollowUserResponseDTO from(User user) {
        return new FollowUserResponseDTO(
            user.getUserId(),
            user.getNickName(),
            user.getUserType()
        );
    }
}
