package com.prothsync.prothsync.dto;

public record TokenRefreshResponseDTO(
    String accessToken
) {
    public static TokenRefreshResponseDTO of(String accessToken) {
        return new TokenRefreshResponseDTO(accessToken);
    }
}
