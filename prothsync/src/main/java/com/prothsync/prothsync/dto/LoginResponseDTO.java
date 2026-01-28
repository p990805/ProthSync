package com.prothsync.prothsync.dto;

public record LoginResponseDTO(
    String accessToken,
    String refreshToken,
    Long userId,
    String userName,
    String nickName
) {
    public static LoginResponseDTO of(String accessToken, String refreshToken,
        Long userId, String userName, String nickName) {
        return new LoginResponseDTO(accessToken, refreshToken, userId, userName, nickName);
    }
}
