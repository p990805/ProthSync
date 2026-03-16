package com.prothsync.prothsync.dto;

public record LoginResponseDTO(
    String accessToken,
    Long userId,
    String userName,
    String nickName
) {
    public static LoginResponseDTO of(String accessToken,
        Long userId, String userName, String nickName) {
        return new LoginResponseDTO(accessToken, userId, userName, nickName);
    }
}
