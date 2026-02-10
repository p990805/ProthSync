package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode implements ErrorCode {

    FOLLOWER_ID_NULL(HttpStatus.BAD_REQUEST, "팔로워 ID가 NULL 입니다."),
    FOLLOWING_ID_NULL(HttpStatus.BAD_REQUEST, "팔로잉 ID가 NULL 입니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}