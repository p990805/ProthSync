package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 프로필 응답 DTO")
public record UserProfileResponseDTO(

    @Schema(description = "사용자 ID")
    Long userId,

    @Schema(description = "닉네임")
    String nickName,

    @Schema(description = "자기소개")
    String bio,

    @Schema(description = "프로필 이미지 URL")
    String profileImageUrl,

    @Schema(description = "사용자 유형")
    UserType userType,

    @Schema(description = "주소")
    String address,

    @Schema(description = "팔로워 수")
    int followerCount,

    @Schema(description = "팔로잉 수")
    int followingCount,

    @Schema(description = "팔로우 여부 (로그인 사용자 기준)")
    boolean isFollowing,

    @Schema(description = "평균 평점")
    double averageRating,

    @Schema(description = "받은 리뷰 수")
    int reviewCount,

    @Schema(description = "가입일")
    LocalDateTime createdAt
) {

    public static UserProfileResponseDTO of(User user, boolean isFollowing) {
        return new UserProfileResponseDTO(
            user.getUserId(),
            user.getNickName(),
            user.getBio(),
            user.getProfileImageUrl(),
            user.getUserType(),
            user.getAddress(),
            user.getFollowerCount(),
            user.getFollowingCount(),
            isFollowing,
            user.getAverageRating(),
            user.getReviewCount(),
            user.getCreatedAt()
        );
    }
}