package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockErrorCode implements ErrorCode {

    BLOCKER_ID_NULL(HttpStatus.BAD_REQUEST, "차단자 ID가 NULL 입니다."),
    BLOCKED_ID_NULL(HttpStatus.BAD_REQUEST, "차단 대상 ID가 NULL 입니다."),
    CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다."),
    ALREADY_BLOCKED(HttpStatus.CONFLICT, "이미 차단한 사용자입니다."),
    BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "차단 정보를 찾을 수 없습니다."),
    BLOCKED_USER(HttpStatus.FORBIDDEN, "차단된 사용자입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
