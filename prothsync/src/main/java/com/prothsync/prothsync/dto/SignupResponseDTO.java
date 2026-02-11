package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SignupResponseDTO(
    Long userId,
    String userName,
    String nickName,
    LocalDate birthday,
    String address,
    Double latitude,
    Double longitude,
    String email,
    UserType userType,
    LocalDateTime createdAt
) {

    public static SignupResponseDTO from(User user) {
        return new SignupResponseDTO(
            user.getUserId(),
            user.getUserName(),
            user.getNickName(),
            user.getBirthday(),
            user.getAddress(),
            user.getLatitude(),
            user.getLongitude(),
            user.getEmail(),
            user.getUserType(),
            user.getCreatedAt()
        );
    }
}