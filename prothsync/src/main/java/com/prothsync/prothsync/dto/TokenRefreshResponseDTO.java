package com.prothsync.prothsync.dto;

public record TokenRefreshResponseDTO(
    String accessToken,
    String refreshToken
) {
    public static TokenRefreshResponseDTO of(String accessToken, String refreshToken) {
        return new TokenRefreshResponseDTO(accessToken, refreshToken);
    }
}
