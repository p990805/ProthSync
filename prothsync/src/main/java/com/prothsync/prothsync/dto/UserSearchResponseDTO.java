package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;

public record UserSearchResponseDTO(
    Long userId,
    String nickName,
    String profileImageUrl,
    UserType userType,
    String bio,
    int followerCount
) {

    public static UserSearchResponseDTO from(User user) {
        return new UserSearchResponseDTO(
            user.getUserId(),
            user.getNickName(),
            user.getProfileImageUrl(),
            user.getUserType(),
            user.getBio(),
            user.getFollowerCount()
        );
    }
}