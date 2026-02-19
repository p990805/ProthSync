package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "내 프로필 응답 DTO")
public record MyProfileResponseDTO(

    @Schema(description = "사용자 ID")
    Long userId,

    @Schema(description = "사용자 아이디")
    String userName,

    @Schema(description = "닉네임")
    String nickName,

    @Schema(description = "자기소개")
    String bio,

    @Schema(description = "프로필 이미지 URL")
    String profileImageUrl,

    @Schema(description = "생년월일")
    LocalDate birthday,

    @Schema(description = "주소")
    String address,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "사용자 유형")
    UserType userType,

    @Schema(description = "팔로워 수")
    int followerCount,

    @Schema(description = "팔로잉 수")
    int followingCount,

    @Schema(description = "위도")
    Double latitude,

    @Schema(description = "경도")
    Double longitude,

    @Schema(description = "가입일")
    LocalDateTime createdAt
) {

    public static MyProfileResponseDTO from(User user) {
        return new MyProfileResponseDTO(
            user.getUserId(),
            user.getUserName(),
            user.getNickName(),
            user.getBio(),
            user.getProfileImageUrl(),
            user.getBirthday(),
            user.getAddress(),
            user.getEmail(),
            user.getUserType(),
            user.getFollowerCount(),
            user.getFollowingCount(),
            user.getLatitude(),
            user.getLongitude(),
            user.getCreatedAt()
        );
    }
}