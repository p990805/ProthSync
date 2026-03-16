package com.prothsync.prothsync.vo;

public record LoginResult(
    String accessToken,
    String refreshToken,
    Long userId,
    String userName,
    String nickName
) {

}
