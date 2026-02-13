package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주변 사용자 응답 DTO")
public record NearbyUserResponseDTO(

    @Schema(description = "사용자 ID")
    Long userId,

    @Schema(description = "닉네임")
    String nickName,

    @Schema(description = "사용자 유형")
    UserType userType,

    @Schema(description = "주소")
    String address,

    @Schema(description = "위도")
    Double latitude,

    @Schema(description = "경도")
    Double longitude,

    @Schema(description = "거리 (km)")
    double distanceKm
) {

    public static NearbyUserResponseDTO of(User user, double distanceKm) {
        return new NearbyUserResponseDTO(
            user.getUserId(),
            user.getNickName(),
            user.getUserType(),
            user.getAddress(),
            user.getLatitude(),
            user.getLongitude(),
            Math.round(distanceKm * 100.0) / 100.0
        );
    }
}